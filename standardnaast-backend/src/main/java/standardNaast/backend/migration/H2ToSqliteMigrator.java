package standardNaast.backend.migration;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.h2.tools.RunScript;

/**
 * Imports an H2 SQL backup into a new desktop SQLite database.
 * The existing PostgreSQL migrator is deliberately not involved in this path.
 */
public final class H2ToSqliteMigrator {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(H2ToSqliteMigrator.class);
    private static final String CHANGELOG = "db/changelog/db.changelog-sqlite.xml";
    private static final Set<String> SAFE_SET_OPTIONS = Set.of(
            "AUTOCOMMIT", "CACHE_SIZE", "DB_CLOSE_DELAY", "DEFAULT_LOCK_TIMEOUT",
            "DEFAULT_NULL_ORDERING", "IGNORECASE", "LOCK_MODE", "MAX_MEMORY_ROWS",
            "MODE", "REFERENTIAL_INTEGRITY", "SCHEMA", "SCHEMA_SEARCH_PATH", "DATABASE_COLLATION",
            "UNDO_LOG", "WRITE_DELAY");
    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "CALL", "RUNSCRIPT", "CSVREAD", "CSVWRITE", "READ_CSV", "FILE_READ",
            "FILE_WRITE", "FILE_DELETE", "FILE_CREATE", "FILE_RENAME", "FILE_COPY",
            "FILE_MOVE", "FILE_TRUNCATE", "LINK_SCHEMA", "LINKED_TABLE",
            "JAVA_OBJECT_SERIALIZER", "SCRIPT", "ENGINE", "INIT");

    @FunctionalInterface
    public interface ProgressListener {
        void onProgress(String message, int percent);
    }

    public record ImportResult(long totalRows, Map<String, Long> rowsPerTable) {}

    private record Column(String name, int jdbcType, String typeName, int nullable,
                          String defaultValue, int primaryKeyOrder) {}
    private record ForeignKey(String table, String column, String referencedTable, String referencedColumn) {}
    private static final Set<ForeignKey> ARCHIVE_RELATIONS = Set.of(
            new ForeignKey("TYPE_MATCH", "TYPE_COMPETITION_ID", "TYPE_COMPETITION", "TYPE_COMPETITION_ID"),
            new ForeignKey("PRIX_PLACE", "TYPE_PERSONNE_ID", "TYPE_PERSONNE", "TYPE_PERSONNE_ID"),
            new ForeignKey("PRIX_PLACE", "TYPE_MATCH_ID", "TYPE_MATCH", "TYPE_MATCH_ID"),
            new ForeignKey("PRIX_PLACE", "SAISON", "SAISON", "SAISON_ID"),
            new ForeignKey("COMMANDE_PLACE", "PERSONNE_ID", "PERSONNES", "PERSONNE_ID"),
            new ForeignKey("COMMANDE_PLACE", "MATCH_ID", "MATCH", "MATCH_ID"),
            new ForeignKey("COMMANDE_PLACE", "BLOC_ID", "BLOC", "BLOC_ID"));
    private static final class ImportValidationException extends IOException {
        private ImportValidationException(String message) {
            super(message);
        }
    }

    private H2ToSqliteMigrator() {}

    public static ImportResult importBackup(Path backup, Path destination, ProgressListener listener) throws Exception {
        Path source = backup.toAbsolutePath().normalize();
        Path target = destination.toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent == null || !Files.isDirectory(parent)) {
            throw new IOException("Le dossier de donnees desktop doit exister avant l'import.");
        }
        if (!Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("La sauvegarde H2 selectionnee n'est pas un fichier regulier.");
        }
        if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
            throw new IOException("La base SQLite existe deja. L'import est refuse afin de proteger les donnees.");
        }

        Path staging = Files.createTempFile(parent, ".standardnaast-import-", ".sqlite");
        Path h2Directory = null;
        try {
            setPrivatePermissions(staging);
            notify(listener, "Preparation du schema SQLite via Liquibase...", 5);
            initializeTargetSchema(staging);
            h2Directory = Files.createTempDirectory(parent, ".standardnaast-h2-restore-");
            setPrivatePermissions(h2Directory);

            ImportResult result;
            try (Connection sourceConnection = openSource(source, h2Directory);
                 Connection targetConnection = DriverManager.getConnection("jdbc:sqlite:" + staging)) {
                try (Statement statement = targetConnection.createStatement()) {
                    statement.execute("PRAGMA foreign_keys=OFF");
                    statement.execute("PRAGMA busy_timeout=10000");
                }
                targetConnection.setAutoCommit(false);

                Map<String, String> targetTables = tableNames(targetConnection);
                Map<String, String> sourceTables = tableNames(sourceConnection);
                validateAndCopyTables(sourceConnection, targetConnection, sourceTables, targetTables, listener);
                targetConnection.commit();

                try (Statement statement = targetConnection.createStatement()) {
                    statement.execute("PRAGMA foreign_keys=ON");
                    validateIntegrity(statement);
                }
            }

            try (FileChannel channel = FileChannel.open(staging, StandardOpenOption.WRITE)) {
                channel.force(true);
            }
            result = summarize(staging);
            if (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("La base SQLite a ete creee pendant l'import. Aucune donnee n'a ete remplacee.");
            }
            try {
                Files.createLink(target, staging);
            } catch (UnsupportedOperationException | IOException e) {
                throw new IOException("Publication atomique impossible sur le dossier de donnees. "
                        + "La base existante n'a pas ete modifiee.", e);
            }
            Files.delete(staging);
            notify(listener, "Import SQLite valide et publie.", 100);
            log.info("Import H2 vers SQLite reussi : {} lignes, {} tables.", result.totalRows(), result.rowsPerTable().size());
            return result;
        } catch (Exception e) {
            if (e instanceof SQLException sql) {
                log.error("Import H2 vers SQLite impossible : erreur SQL, etat {}, code {}.",
                        sql.getSQLState(), sql.getErrorCode());
            } else {
                log.error("Import H2 vers SQLite impossible : {}", safeFailureMessage(e, source, target));
            }
            throw e;
        } finally {
            deleteSqliteSidecars(staging);
            Files.deleteIfExists(staging);
            if (h2Directory != null) {
                deleteTree(h2Directory);
            }
        }
    }

    private static ImportResult summarize(Path database) throws SQLException {
        Map<String, Long> rows = new LinkedHashMap<>();
        long total = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + database)) {
            for (String table : tableNames(connection).values()) {
                long count = count(connection, table);
                rows.put(table, count);
                total += count;
            }
        }
        return new ImportResult(total, Map.copyOf(rows));
    }

    private static void initializeTargetSchema(Path staging) throws Exception {
        var dataSource = new DriverManagerDataSource("jdbc:sqlite:" + staging, "", "");
        var liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(CHANGELOG);
        liquibase.afterPropertiesSet();
    }

    private static Connection openSource(Path source, Path h2Directory) throws Exception {
        if (isSqlBackup(source)) {
            String url = "jdbc:h2:file:" + h2Directory.resolve("restored").toAbsolutePath()
                    + ";DB_CLOSE_DELAY=0;NON_KEYWORDS=VALUE,USER,ROLE";
            String sql;
            try {
                sql = Files.readString(source);
            } catch (IOException e) {
                throw new IOException("Impossible de lire le fichier de sauvegarde SQL H2.");
            }
            if (sql.startsWith("\uFEFF")) sql = sql.substring(1);
            String safeScript = validateAndTransformScript(sql);
            try (Connection connection = DriverManager.getConnection(url, "sa", "");
                 StringReader reader = new StringReader(safeScript)) {
                try {
                    RunScript.execute(connection, reader);
                } catch (SQLException e) {
                    throw new ImportValidationException("La sauvegarde SQL H2 est invalide ou incompatible "
                            + "(erreur H2 " + e.getErrorCode() + ").");
                } finally {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute("SET DB_CLOSE_DELAY 0");
                    }
                }
            }
            return DriverManager.getConnection(url, "sa", "");
        }

        throw new IOException("Selectionnez une sauvegarde SQL H2, pas un fichier de base de donnees.");
    }

    private static boolean isSqlBackup(Path source) throws IOException {
        String name = source.getFileName().toString().toLowerCase(Locale.ROOT);
        if (name.endsWith(".mv.db") || name.endsWith(".h2.db")) {
            return false;
        }
        if (name.endsWith(".sql")) {
            return true;
        }
        return H2ToPostgresMigrator.isH2SqlScript(source.toString());
    }

    private static void validateAndCopyTables(Connection source, Connection target,
                                              Map<String, String> sourceTables,
                                              Map<String, String> targetTables,
                                              ProgressListener listener) throws Exception {
        if (sourceTables.isEmpty()) {
            throw new IOException("La sauvegarde H2 ne contient aucune table applicative.");
        }
        Map<String, String> targetLookup = new HashMap<>();
        targetTables.forEach((key, value) -> targetLookup.put(key.toUpperCase(Locale.ROOT), value));

        List<String> tablesToImport = new ArrayList<>();
        for (String sourceName : sourceTables.values()) {
            if (isLiquibaseTable(sourceName)) {
                continue;
            }
            String targetName = targetLookup.get(sourceName.toUpperCase(Locale.ROOT));
            if (targetName == null) {
                throw new IOException("Table H2 non prise en charge : " + sourceName
                        + ". L'import est interrompu plutot que de perdre son historique.");
            }
            tablesToImport.add(sourceName);
        }
        if (tablesToImport.isEmpty()) {
            throw new IOException("La sauvegarde H2 ne contient aucune table applicative importable.");
        }
        tablesToImport.sort(Comparator.comparing(String::toUpperCase));

        Map<String, String> targetLookupExact = new HashMap<>();
        targetTables.forEach((key, value) -> targetLookupExact.put(key.toUpperCase(Locale.ROOT), value));
        long totalRows = 0;
        int index = 0;
        Map<String, Long> sourceCounts = new LinkedHashMap<>();
        for (String sourceName : tablesToImport) {
            String targetName = targetLookupExact.get(sourceName.toUpperCase(Locale.ROOT));
            List<Column> sourceColumns = columns(source, sourceName);
            List<Column> targetColumns = columns(target, targetName);
            validateColumns(sourceName, sourceColumns, targetColumns);
            validateForeignKeys(source, target, sourceName, targetName);
            long sourceCount = count(source, sourceName);
            sourceCounts.put(targetName, sourceCount);
            totalRows += sourceCount;
        }

        long copied = 0;
        for (String sourceName : tablesToImport) {
            String targetName = targetLookupExact.get(sourceName.toUpperCase(Locale.ROOT));
            List<Column> sourceColumns = columns(source, sourceName);
            List<Column> targetColumns = columns(target, targetName);
            long sourceCount = sourceCounts.get(targetName);
            notify(listener, "Import de " + sourceName + " (" + sourceCount + " lignes)...",
                    15 + (int) (70.0 * index / tablesToImport.size()));
            String validWhere = "";
            long archivedCount = 0;
            if (sourceName.equalsIgnoreCase("PERSONNES_COTISATIONS") || sourceName.equalsIgnoreCase("PERSONNE_MATCH")) {
                String orphan = quote(sourceName) + ".\"PERSONNE_ID\" IS NOT NULL AND NOT EXISTS "
                        + "(SELECT 1 FROM \"PERSONNES\" p WHERE p.\"PERSONNE_ID\" = "
                        + quote(sourceName) + ".\"PERSONNE_ID\")";
                String archiveWhere = " WHERE " + orphan;
                archivedCount = queryCount(source, "SELECT COUNT(*) FROM " + quote(sourceName) + archiveWhere);
                if (archivedCount > 0) {
                    String archiveName = targetName + "_IMPORT_ARCHIVE";
                    List<Column> archiveColumns = columns(target, archiveName);
                    validateColumns(sourceName, sourceColumns, archiveColumns);
                    copyAndVerify(source, target, sourceName, archiveName, sourceColumns, archiveColumns,
                            archivedCount, archiveWhere);
                    notify(listener, archivedCount + " lignes de " + sourceName
                                    + " conservees dans " + archiveName + " (personne absente).",
                            15 + (int) (70.0 * index / tablesToImport.size()));
                    log.warn("[DESKTOP_IMPORT] {} lignes de {} archivees dans {} : PERSONNE_ABSENTE. "
                            + "Aucune ligne supprimee ni personne fictive creee.", archivedCount, sourceName, archiveName);
                    validWhere = " WHERE NOT (" + orphan + ")";
                }
            }
            copyAndVerify(source, target, sourceName, targetName, sourceColumns, targetColumns,
                    sourceCount - archivedCount, validWhere);
            copied += sourceCount;
            index++;
        }
        if (copied != totalRows) {
            throw new SQLException("Le nombre de lignes copie ne correspond pas au nombre de lignes source.");
        }
        for (ForeignKey relation : ARCHIVE_RELATIONS) {
            long orphans = countUnmatchedReferences(target, relation);
            if (orphans > 0) {
                log.warn("[DESKTOP_IMPORT] Archive {}.{} : {} references sans ligne dans {}. "
                                + "Les valeurs historiques sont conservees sans modification.",
                        relation.table(), relation.column(), orphans, relation.referencedTable());
            }
        }
    }

    private static void copyAndVerify(Connection source, Connection target, String sourceName, String targetName,
                                      List<Column> sourceColumns, List<Column> targetColumns,
                                      long sourceCount, String sourceWhere) throws Exception {
        copyTable(source, target, sourceName, targetName, sourceColumns, targetColumns, sourceWhere);
        assertRowCountAndKeys(source, target, sourceName, targetName, sourceColumns, targetColumns, sourceCount, sourceWhere);
        assertValues(source, target, sourceName, targetName, sourceColumns, sourceWhere);
    }

    private static void validateColumns(String table, List<Column> source, List<Column> target) throws IOException {
        Map<String, Column> sourceByName = new HashMap<>();
        for (Column column : source) {
            sourceByName.put(column.name().toUpperCase(Locale.ROOT), column);
        }
        Map<String, Column> targetByName = new HashMap<>();
        for (Column column : target) {
            targetByName.put(column.name().toUpperCase(Locale.ROOT), column);
        }
        for (Column column : source) {
            Column destination = targetByName.get(column.name().toUpperCase(Locale.ROOT));
            if (destination == null) {
                throw new ImportValidationException("Colonne H2 non prise en charge dans " + table + " : " + column.name()
                        + ". Aucune colonne source ne sera ignoree.");
            }
            String type = column.typeName() == null ? "" : column.typeName().toUpperCase(Locale.ROOT);
            if (column.jdbcType() == Types.JAVA_OBJECT || type.contains("JAVA_OBJECT")
                    || type.contains("GEOMETRY") || type.contains("ARRAY")
                    || type.contains("ROW") || type.contains("STRUCT")) {
                throw new ImportValidationException("Type H2 non pris en charge dans " + table + "." + column.name()
                        + " : " + column.typeName() + ".");
            }
            if (!compatibleTypes(column, destination) && !isLegacyPricePosition(table, column, destination)) {
                throw new ImportValidationException("Types incompatibles pour " + table + "." + column.name()
                        + " : H2 " + column.typeName() + ", SQLite " + destination.typeName() + ".");
            }
        }
        for (Column column : target) {
            if (sourceByName.containsKey(column.name().toUpperCase(Locale.ROOT))) {
                continue;
            }
            if (column.primaryKeyOrder() > 0) {
                throw new ImportValidationException("Cle primaire absente de la table H2 " + table + " : " + column.name());
            }
            if (column.nullable() == DatabaseMetaData.columnNoNulls && column.defaultValue() == null) {
                throw new ImportValidationException("La colonne obligatoire " + table + "." + column.name()
                        + " est absente de la sauvegarde et n'a pas de valeur par defaut.");
            }
        }
    }

    private static boolean isLegacyPricePosition(String table, Column source, Column target) {
        return table.equalsIgnoreCase("PRICES")
                && (source.name().equalsIgnoreCase("RANG") || source.name().equalsIgnoreCase("PLACE"))
                && Set.of(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT, Types.NUMERIC, Types.DECIMAL)
                        .contains(source.jdbcType())
                && target.typeName().toUpperCase(Locale.ROOT).contains("CHAR");
    }

    private static boolean compatibleTypes(Column source, Column target) {
        String sourceName = source.typeName() == null ? "" : source.typeName().toUpperCase(Locale.ROOT);
        String targetName = target.typeName() == null ? "" : target.typeName().toUpperCase(Locale.ROOT);
        boolean sourceNumeric = Set.of(Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                Types.NUMERIC, Types.DECIMAL, Types.FLOAT, Types.REAL, Types.DOUBLE, Types.BIT, Types.BOOLEAN)
                .contains(source.jdbcType());
        boolean targetNumeric = targetName.contains("INT") || targetName.contains("NUM")
                || targetName.contains("DECIMAL") || targetName.contains("REAL")
                || targetName.contains("FLOAT") || targetName.contains("DOUBLE");
        boolean sourceText = Set.of(Types.CHAR, Types.VARCHAR, Types.LONGVARCHAR, Types.NCHAR,
                Types.NVARCHAR, Types.LONGNVARCHAR, Types.CLOB, Types.NCLOB)
                .contains(source.jdbcType()) || sourceName.equals("ENUM");
        boolean targetText = targetName.contains("CHAR") || targetName.contains("TEXT")
                || targetName.contains("CLOB");
        boolean sourceTemporal = Set.of(Types.DATE, Types.TIME, Types.TIME_WITH_TIMEZONE,
                Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE)
                .contains(source.jdbcType());
        boolean targetTemporal = targetName.contains("DATE") || targetName.contains("TIME");
        boolean targetBoolean = targetName.contains("BOOL");
        boolean sourceBoolean = source.jdbcType() == Types.BOOLEAN || source.jdbcType() == Types.BIT;
        if (targetBoolean) return sourceBoolean || sourceNumeric;
        if (targetNumeric) return sourceNumeric;
        if (targetText) return sourceText;
        if (targetTemporal) return sourceTemporal;
        return sourceName.equals(targetName);
    }

    private static void validateForeignKeys(Connection source, Connection target,
                                            String sourceTable, String targetTable) throws Exception {
        Set<ForeignKey> sourceKeys = foreignKeys(source, sourceTable);
        Set<ForeignKey> targetKeys = foreignKeys(target, targetTable);
        for (ForeignKey foreignKey : sourceKeys) {
            boolean present = targetKeys.stream().anyMatch(targetKey ->
                    targetKey.column().equalsIgnoreCase(foreignKey.column())
                            && targetKey.referencedTable().equalsIgnoreCase(foreignKey.referencedTable())
                            && targetKey.referencedColumn().equalsIgnoreCase(foreignKey.referencedColumn()));
            if (!present) {
                ForeignKey normalized = new ForeignKey(foreignKey.table().toUpperCase(Locale.ROOT),
                        foreignKey.column().toUpperCase(Locale.ROOT),
                        foreignKey.referencedTable().toUpperCase(Locale.ROOT),
                        foreignKey.referencedColumn().toUpperCase(Locale.ROOT));
                if (ARCHIVE_RELATIONS.contains(normalized)) {
                    if (countUnmatchedReferences(source, foreignKey) > 0) {
                        throw new ImportValidationException("La sauvegarde contient une relation invalide declaree : "
                                + sourceTable + "." + foreignKey.column() + " -> "
                                + foreignKey.referencedTable() + "." + foreignKey.referencedColumn());
                    }
                    continue;
                }
                throw new ImportValidationException("Cle etrangere historique non representee par le schema SQLite : "
                        + sourceTable + "." + foreignKey.column() + " -> "
                        + foreignKey.referencedTable() + "." + foreignKey.referencedColumn());
            }
        }
    }

    private static long countUnmatchedReferences(Connection connection, ForeignKey relation) throws SQLException {
        String value = "child." + quote(relation.column());
        String parent = "parent." + quote(relation.referencedColumn());
        String sql = "SELECT COUNT(*) FROM " + quote(relation.table()) + " child WHERE " + value
                + " IS NOT NULL AND NOT EXISTS (SELECT 1 FROM " + quote(relation.referencedTable())
                + " parent WHERE " + parent + " = " + value + ")";
        try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            rows.next();
            return rows.getLong(1);
        }
    }

    private static void copyTable(Connection source, Connection target, String sourceTable, String targetTable,
                                  List<Column> sourceColumns, List<Column> targetColumns,
                                  String sourceWhere) throws SQLException, IOException {
        Map<String, Column> targetByName = new HashMap<>();
        targetColumns.forEach(column -> targetByName.put(column.name().toUpperCase(Locale.ROOT), column));
        List<Column> columns = sourceColumns.stream()
                .filter(column -> targetByName.containsKey(column.name().toUpperCase(Locale.ROOT)))
                .toList();
        String names = columns.stream().map(column -> quote(column.name())).collect(java.util.stream.Collectors.joining(","));
        String placeholders = String.join(",", java.util.Collections.nCopies(columns.size(), "?"));
        String select = "SELECT " + names + " FROM " + quote(sourceTable) + sourceWhere;
        String insert = "INSERT INTO " + quote(targetTable) + " (" + names + ") VALUES (" + placeholders + ")";
        try (Statement query = source.createStatement();
             ResultSet rows = query.executeQuery(select);
             PreparedStatement statement = target.prepareStatement(insert)) {
            int batchSize = 0;
            while (rows.next()) {
                for (int i = 0; i < columns.size(); i++) {
                    setValue(statement, i + 1, rows.getObject(i + 1), columns.get(i).jdbcType(),
                            targetByName.get(columns.get(i).name().toUpperCase(Locale.ROOT)));
                }
                statement.addBatch();
                if (++batchSize == 500) {
                    statement.executeBatch();
                    batchSize = 0;
                }
            }
            if (batchSize > 0) {
                statement.executeBatch();
            }
        }
    }

    private static void setValue(PreparedStatement statement, int index, Object value, int jdbcType,
                                 Column targetColumn) throws SQLException, IOException {
        if (value == null) {
            statement.setNull(index, jdbcType);
        } else if (value instanceof Number number && targetColumn.typeName().toUpperCase(Locale.ROOT).contains("CHAR")) {
            statement.setString(index, new BigDecimal(number.toString()).stripTrailingZeros().toPlainString());
        } else if (targetColumn.typeName().toUpperCase(Locale.ROOT).contains("BOOL") && value instanceof Number number) {
            BigDecimal decimal = new BigDecimal(number.toString());
            if (decimal.compareTo(BigDecimal.ZERO) != 0 && decimal.compareTo(BigDecimal.ONE) != 0) {
                throw new ImportValidationException("Valeur booleenne invalide pour " + targetColumn.name() + ".");
            }
            statement.setInt(index, decimal.intValue());
        } else if (value instanceof Number number && targetColumn.typeName().toUpperCase(Locale.ROOT).contains("INT")) {
            try {
                statement.setLong(index, new BigDecimal(number.toString()).longValueExact());
            } catch (ArithmeticException | NumberFormatException e) {
                throw new ImportValidationException("Valeur entiere non representable pour " + targetColumn.name() + ".");
            }
        } else if (value instanceof BigDecimal decimal) {
            statement.setString(index, decimal.toPlainString());
        } else if (value instanceof Boolean bool) {
            statement.setInt(index, bool ? 1 : 0);
        } else if (value instanceof java.sql.Date date) {
            statement.setDate(index, date, utcCalendar());
        } else if (value instanceof Timestamp timestamp) {
            statement.setTimestamp(index, timestamp, utcCalendar());
        } else if (value instanceof Clob clob) {
            long length = clob.length();
            if (length > Integer.MAX_VALUE) {
                throw new SQLException("Champ texte H2 trop volumineux pour la colonne SQLite.");
            }
            statement.setString(index, clob.getSubString(1, (int) length));
        } else if (value instanceof LocalDate date) {
            statement.setDate(index, java.sql.Date.valueOf(date), utcCalendar());
        } else if (value instanceof LocalDateTime dateTime) {
            statement.setTimestamp(index, Timestamp.valueOf(dateTime), utcCalendar());
        } else {
            statement.setObject(index, value);
        }
    }

    private static java.util.Calendar utcCalendar() {
        return java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"));
    }

    private static void assertValues(Connection source, Connection target, String sourceTable, String targetTable,
                                     List<Column> sourceColumns, String sourceWhere) throws Exception {
        String names = sourceColumns.stream().map(column -> quote(column.name()))
                .collect(java.util.stream.Collectors.joining(","));
        String keys = sourceColumns.stream().filter(column -> column.primaryKeyOrder() > 0)
                .sorted(Comparator.comparingInt(Column::primaryKeyOrder)).map(column -> quote(column.name()))
                .collect(java.util.stream.Collectors.joining(","));
        try (Statement sourceQuery = source.createStatement();
             Statement targetQuery = target.createStatement();
             ResultSet original = sourceQuery.executeQuery("SELECT " + names + " FROM " + quote(sourceTable) + sourceWhere + " ORDER BY " + keys);
             ResultSet imported = targetQuery.executeQuery("SELECT " + names + " FROM " + quote(targetTable) + " ORDER BY " + keys)) {
            while (original.next()) {
                if (!imported.next()) {
                    throw new ImportValidationException("Ligne manquante apres import de " + targetTable + ".");
                }
                for (int i = 0; i < sourceColumns.size(); i++) {
                    Column column = sourceColumns.get(i);
                    Object expected = original.getObject(i + 1);
                    Object actual = imported.getObject(i + 1);
                    if (!sameValue(expected, actual)) {
                        throw new ImportValidationException("Valeur alteree apres import de " + targetTable + "." + column.name()
                                + ". La base SQLite n'a pas ete publiee.");
                    }
                }
            }
            if (imported.next()) {
                throw new ImportValidationException("Lignes supplementaires apres import de " + targetTable + ".");
            }
        }
    }

    private static boolean sameValue(Object expected, Object actual) throws Exception {
        if (expected == null || actual == null) return expected == actual;
        if (expected instanceof Clob clob) {
            expected = clob.getSubString(1, Math.toIntExact(clob.length()));
        }
        if (expected instanceof java.sql.Date date) {
            expected = date.getTime();
        } else if (expected instanceof Timestamp timestamp) {
            if (timestamp.getNanos() % 1_000_000 != 0) return false;
            expected = timestamp.getTime();
        } else if (expected instanceof Boolean bool) {
            expected = bool ? 1 : 0;
        }
        if (expected instanceof Number number) {
            return new BigDecimal(number.toString()).compareTo(new BigDecimal(actual.toString())) == 0;
        }
        if (expected instanceof byte[] bytes && actual instanceof byte[] imported) {
            return java.util.Arrays.equals(bytes, imported);
        }
        return expected.toString().equals(actual.toString());
    }

    private static void assertRowCountAndKeys(Connection source, Connection target, String sourceTable,
                                              String targetTable, List<Column> sourceColumns,
                                              List<Column> targetColumns, long expectedCount,
                                              String sourceWhere) throws SQLException {
        long sourceCount = queryCount(source, "SELECT COUNT(*) FROM " + quote(sourceTable) + sourceWhere);
        long targetCount = count(target, targetTable);
        if (sourceCount != expectedCount || targetCount != expectedCount) {
            throw new SQLException("Nombre de lignes invalide pour " + targetTable
                    + " : source=" + sourceCount + ", SQLite=" + targetCount + ".");
        }
        List<Column> sourceKeys = sourceColumns.stream().filter(column -> column.primaryKeyOrder() > 0)
                .sorted(Comparator.comparingInt(Column::primaryKeyOrder)).toList();
        List<Column> targetKeys = targetColumns.stream().filter(column -> column.primaryKeyOrder() > 0)
                .sorted(Comparator.comparingInt(Column::primaryKeyOrder)).toList();
        if (sourceKeys.isEmpty() || sourceKeys.size() != targetKeys.size()) {
            throw new SQLException("Cle primaire non comparable apres import de " + targetTable + ".");
        }
        String sourceKeySql = keyQuery(sourceTable, sourceKeys, sourceWhere);
        String targetKeySql = keyQuery(targetTable, targetKeys, "");
        List<List<String>> sourceValues = keyValues(source, sourceKeySql, sourceKeys.size());
        List<List<String>> targetValues = keyValues(target, targetKeySql, targetKeys.size());
        if (!sourceValues.equals(targetValues)) {
            throw new SQLException("Les identifiants de " + targetTable + " ne correspondent pas aux valeurs source.");
        }
    }

    private static String keyQuery(String table, List<Column> keys, String where) {
        String names = keys.stream().map(column -> quote(column.name()))
                .collect(java.util.stream.Collectors.joining(","));
        return "SELECT " + names + " FROM " + quote(table) + where + " ORDER BY " + names;
    }

    private static List<List<String>> keyValues(Connection connection, String sql, int columns) throws SQLException {
        List<List<String>> values = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rows = statement.executeQuery(sql)) {
            while (rows.next()) {
                List<String> key = new ArrayList<>();
                for (int i = 1; i <= columns; i++) {
                    key.add(canonical(rows.getObject(i)));
                }
                values.add(key);
            }
        }
        return values;
    }

    private static String canonical(Object value) {
        if (value == null) return "<null>";
        if (value instanceof BigDecimal decimal) return decimal.stripTrailingZeros().toPlainString();
        if (value instanceof java.sql.Date date) return date.toLocalDate().toString();
        if (value instanceof Timestamp timestamp) return timestamp.toLocalDateTime().toString();
        return value.toString();
    }

    private static List<Column> columns(Connection connection, String table) throws SQLException {
        Map<String, Integer> primaryKeys = new HashMap<>();
        try (ResultSet keys = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), null, table)) {
            while (keys.next()) {
                primaryKeys.put(keys.getString("COLUMN_NAME").toUpperCase(Locale.ROOT), keys.getInt("KEY_SEQ"));
            }
        }
        List<Column> columns = new ArrayList<>();
        try (ResultSet result = connection.getMetaData().getColumns(connection.getCatalog(), null, table, "%")) {
            while (result.next()) {
                String name = result.getString("COLUMN_NAME");
                columns.add(new Column(name, result.getInt("DATA_TYPE"), result.getString("TYPE_NAME"), result.getInt("NULLABLE"),
                        result.getString("COLUMN_DEF"), primaryKeys.getOrDefault(name.toUpperCase(Locale.ROOT), 0)));
            }
        }
        if (columns.isEmpty()) {
            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery("PRAGMA table_info(" + quote(table) + ")")) {
                while (result.next()) {
                    String name = result.getString("name");
                    boolean notNull = result.getInt("notnull") == 1;
                    int pk = result.getInt("pk");
                    columns.add(new Column(name, Types.OTHER, result.getString("type"),
                            notNull ? DatabaseMetaData.columnNoNulls : DatabaseMetaData.columnNullable,
                            result.getString("dflt_value"), pk));
                }
            }
        }
        return columns;
    }

    private static Map<String, String> tableNames(Connection connection) throws SQLException {
        Map<String, String> names = new LinkedHashMap<>();
        try (ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String name = tables.getString("TABLE_NAME");
                if (name == null || name.toLowerCase(Locale.ROOT).startsWith("sqlite_")
                        || isLiquibaseTable(name)) {
                    continue;
                }
                String schema = tables.getString("TABLE_SCHEM");
                if (connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("H2")) {
                    if ("INFORMATION_SCHEMA".equalsIgnoreCase(schema)) continue;
                    if (!"PUBLIC".equalsIgnoreCase(schema)) {
                        throw new SQLException("Schema H2 non pris en charge : " + schema + ".");
                    }
                }
                String key = name.toUpperCase(Locale.ROOT);
                if (names.containsKey(key)) {
                    throw new SQLException("Nom de table ambigu dans la source : " + name + ".");
                }
                names.put(key, name);
            }
        }
        return names;
    }

    private static Set<ForeignKey> foreignKeys(Connection connection, String table) throws SQLException {
        Set<ForeignKey> keys = new HashSet<>();
        try (ResultSet rows = connection.getMetaData().getImportedKeys(connection.getCatalog(), null, table)) {
            while (rows.next()) {
                keys.add(new ForeignKey(rows.getString("FKTABLE_NAME"), rows.getString("FKCOLUMN_NAME"),
                        rows.getString("PKTABLE_NAME"), rows.getString("PKCOLUMN_NAME")));
            }
        }
        if (keys.isEmpty() && connection.getMetaData().getDatabaseProductName().equalsIgnoreCase("SQLite")) {
            try (Statement statement = connection.createStatement();
                 ResultSet rows = statement.executeQuery("PRAGMA foreign_key_list(" + quote(table) + ")")) {
                while (rows.next()) {
                    keys.add(new ForeignKey(table, rows.getString("from"), rows.getString("table"), rows.getString("to")));
                }
            }
        }
        return keys;
    }

    private static long count(Connection connection, String table) throws SQLException {
        return queryCount(connection, "SELECT COUNT(*) FROM " + quote(table));
    }

    private static long queryCount(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getLong(1);
        }
    }

    private static void validateIntegrity(Statement statement) throws Exception {
        try (ResultSet result = statement.executeQuery("PRAGMA integrity_check")) {
            if (!result.next() || !"ok".equalsIgnoreCase(result.getString(1))) {
                throw new ImportValidationException("Echec PRAGMA integrity_check de la base SQLite.");
            }
        }
        try (ResultSet result = statement.executeQuery("PRAGMA foreign_key_check")) {
            if (result.next()) {
                throw new ImportValidationException("La sauvegarde contient une relation invalide : "
                        + result.getString("table") + " ligne " + result.getString("rowid")
                        + " vers " + result.getString("parent") + ".");
            }
        }
    }

    private static boolean isLiquibaseTable(String name) {
        return name.equalsIgnoreCase("DATABASECHANGELOG") || name.equalsIgnoreCase("DATABASECHANGELOGLOCK");
    }

    private static String quote(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private static void notify(ProgressListener listener, String message, int percent) {
        log.info("[DESKTOP_IMPORT] {}% - {}", percent, message);
        if (listener != null) listener.onProgress(message, percent);
    }

    private static String safeFailureMessage(Exception error, Path source, Path target) {
        String message = error.getMessage();
        if (error instanceof ImportValidationException && message != null) {
            return message;
        }
        if (error instanceof IOException && message != null
                && !message.contains(source.toString()) && !message.contains(target.toString())) {
            return message;
        }
        return "Erreur " + error.getClass().getSimpleName() + ".";
    }

    private static void setPrivatePermissions(Path path) throws IOException {
        if (Files.getFileStore(path).supportsFileAttributeView("posix")) {
            Files.setPosixFilePermissions(path, java.nio.file.attribute.PosixFilePermissions.fromString(
                    Files.isDirectory(path) ? "rwx------" : "rw-------"));
        }
    }

    private static void deleteSqliteSidecars(Path database) throws IOException {
        Files.deleteIfExists(Path.of(database + "-wal"));
        Files.deleteIfExists(Path.of(database + "-shm"));
        Files.deleteIfExists(Path.of(database + "-journal"));
    }

    private static void deleteTree(Path directory) throws IOException {
        if (!Files.exists(directory)) return;
        try (var paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static String validateAndTransformScript(String script) throws IOException {
        List<String> statements = splitStatements(script);
        List<String> accepted = new ArrayList<>();
        for (String statement : statements) {
            List<String> words = sqlWords(statement);
            if (words.isEmpty()) continue;
            rejectUnsafeWords(words);
            if (words.contains("SELECT")) {
                throw new IOException("Les requetes SELECT ne sont pas autorisees dans une sauvegarde SQL H2.");
            }
            String first = words.get(0);
            boolean allowed = switch (first) {
                case "SET" -> words.size() > 1 && (SAFE_SET_OPTIONS.contains(words.get(1))
                        || words.get(1).equals("DATABASE") && words.size() > 2
                        && words.get(2).equals("COLLATION"));
                case "CREATE" -> safeCreate(words);
                case "ALTER" -> words.size() > 2
                        && (words.get(1).equals("TABLE") || words.get(1).equals("SEQUENCE"));
                case "INSERT" -> words.size() > 2 && words.get(1).equals("INTO") && !words.contains("SELECT");
                case "GRANT", "COMMENT" -> true;
                default -> false;
            };
            if (!allowed) {
                throw new IOException("Instruction SQL H2 refusee pour des raisons de securite : "
                        + String.join(" ", words.subList(0, Math.min(3, words.size()))));
            }
            accepted.add(transformLegacyTypes(statement, first.equals("CREATE") && words.contains("TABLE")));
        }
        if (accepted.isEmpty()) {
            throw new IOException("La sauvegarde SQL H2 ne contient aucune instruction d'import autorisee.");
        }
        return String.join(";\n", accepted) + ";\n";
    }

    private static boolean safeCreate(List<String> words) {
        if (words.size() < 2) return false;
        return switch (words.get(1)) {
            case "USER", "SEQUENCE", "TABLE", "INDEX" -> true;
            case "MEMORY", "CACHED" -> words.size() > 2 && words.get(2).equals("TABLE");
            case "UNIQUE" -> words.size() > 2 && words.get(2).equals("INDEX");
            default -> false;
        };
    }

    private static void rejectUnsafeWords(List<String> words) throws IOException {
        for (String word : words) {
            if (FORBIDDEN_WORDS.contains(word)) {
                throw new IOException("La sauvegarde SQL contient une instruction externe ou non securisee : " + word);
            }
        }
    }

    private static List<String> splitStatements(String sql) throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean single = false, doubleQuote = false, backtick = false, bracket = false, lineComment = false;
        int blockDepth = 0;
        for (int i = 0; i < sql.length(); i++) {
            char ch = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';
            current.append(ch);
            if (lineComment) {
                if (ch == '\n') lineComment = false;
                continue;
            }
            if (blockDepth > 0) {
                if (ch == '/' && next == '*') {
                    current.append(next);
                    i++;
                    blockDepth++;
                } else if (ch == '*' && next == '/') {
                    current.append(next);
                    i++;
                    blockDepth--;
                }
                continue;
            }
            if (!single && !doubleQuote && !backtick && !bracket) {
                if (ch == '-' && next == '-') {
                    current.append(next);
                    i++;
                    lineComment = true;
                } else if (ch == '/' && next == '*') {
                    current.append(next);
                    i++;
                    blockDepth = 1;
                } else if (ch == ';') {
                    statements.add(current.substring(0, current.length() - 1));
                    current.setLength(0);
                } else if (ch == '\'') single = true;
                else if (ch == '"') doubleQuote = true;
                else if (ch == '`') backtick = true;
                else if (ch == '[') bracket = true;
            } else if (single && ch == '\'') {
                if (next == '\'') {
                    current.append(next);
                    i++;
                } else single = false;
            } else if (doubleQuote && ch == '"') {
                if (next == '"') {
                    current.append(next);
                    i++;
                } else doubleQuote = false;
            } else if (backtick && ch == '`') backtick = false;
            else if (bracket && ch == ']') bracket = false;
        }
        if (single || doubleQuote || backtick || bracket || blockDepth > 0) {
            throw new IOException("La sauvegarde SQL H2 contient une chaine ou un commentaire non termine.");
        }
        if (!current.toString().isBlank()) statements.add(current.toString());
        return statements;
    }

    private static List<String> sqlWords(String sql) {
        List<String> words = new ArrayList<>();
        boolean single = false, doubleQuote = false, backtick = false, bracket = false, lineComment = false;
        int blockDepth = 0;
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < sql.length(); i++) {
            char ch = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';
            if (lineComment) {
                if (ch == '\n') lineComment = false;
                continue;
            }
            if (blockDepth > 0) {
                if (ch == '/' && next == '*') { i++; blockDepth++; }
                else if (ch == '*' && next == '/') { i++; blockDepth--; }
                continue;
            }
            if (!single && !doubleQuote && !backtick && !bracket) {
                if (ch == '-' && next == '-') { flushWord(word, words); i++; lineComment = true; continue; }
                if (ch == '/' && next == '*') { flushWord(word, words); i++; blockDepth = 1; continue; }
                if (ch == '\'') { flushWord(word, words); single = true; continue; }
                if (ch == '"') { flushWord(word, words); doubleQuote = true; continue; }
                if (ch == '`') { flushWord(word, words); backtick = true; continue; }
                if (ch == '[') { flushWord(word, words); bracket = true; continue; }
                if (Character.isLetterOrDigit(ch) || ch == '_' || ch == '$') word.append(Character.toUpperCase(ch));
                else flushWord(word, words);
            } else if (single && ch == '\'') {
                if (next == '\'') i++; else single = false;
            } else if (doubleQuote && ch == '"') {
                if (next == '"') {
                    word.append(ch);
                    i++;
                } else {
                    doubleQuote = false;
                    flushWord(word, words);
                }
            } else if (backtick && ch == '`') {
                backtick = false;
                flushWord(word, words);
            } else if (bracket && ch == ']') {
                bracket = false;
                flushWord(word, words);
            } else if (doubleQuote || backtick || bracket) {
                word.append(Character.toUpperCase(ch));
            }
        }
        flushWord(word, words);
        return words;
    }

    private static void flushWord(StringBuilder word, List<String> words) {
        if (word.length() > 0) {
            words.add(word.toString());
            word.setLength(0);
        }
    }

    private static String transformLegacyTypes(String statement, boolean tableDdl) {
        if (!tableDdl) return statement;
        StringBuilder output = new StringBuilder(statement.length());
        boolean single = false, doubleQuote = false, backtick = false, bracket = false, lineComment = false;
        int blockDepth = 0;
        boolean inColumnList = false;
        int parenDepth = 0;
        for (int i = 0; i < statement.length();) {
            char ch = statement.charAt(i);
            char next = i + 1 < statement.length() ? statement.charAt(i + 1) : '\0';
            if (lineComment) {
                output.append(ch);
                i++;
                if (ch == '\n') lineComment = false;
                continue;
            }
            if (blockDepth > 0) {
                output.append(ch);
                i++;
                if (ch == '/' && next == '*') { output.append(next); i++; blockDepth++; }
                else if (ch == '*' && next == '/') { output.append(next); i++; blockDepth--; }
                continue;
            }
            if (!single && !doubleQuote && !backtick && !bracket) {
                if (ch == '-' && next == '-') { output.append(ch).append(next); i += 2; lineComment = true; continue; }
                if (ch == '/' && next == '*') { output.append(ch).append(next); i += 2; blockDepth = 1; continue; }
                if (ch == '\'') { output.append(ch); i++; single = true; continue; }
                if (ch == '"') { output.append(ch); i++; doubleQuote = true; continue; }
                if (ch == '`') { output.append(ch); i++; backtick = true; continue; }
                if (ch == '[') { output.append(ch); i++; bracket = true; continue; }
                if (ch == '(') {
                    parenDepth++;
                    if (!inColumnList && parenDepth == 1) inColumnList = true;
                    output.append(ch);
                    i++;
                    continue;
                }
                if (ch == ')') { parenDepth--; output.append(ch); i++; continue; }
                if (Character.isLetter(ch) || ch == '_') {
                    int end = i + 1;
                    while (end < statement.length()) {
                        char value = statement.charAt(end);
                        if (!Character.isLetterOrDigit(value) && value != '_' && value != '$') break;
                        end++;
                    }
                    String word = statement.substring(i, end);
                    if (inColumnList && parenDepth == 1 && word.equalsIgnoreCase("DECIMAL")) {
                        int p = end;
                        while (p < statement.length() && Character.isWhitespace(statement.charAt(p))) p++;
                        if (p < statement.length() && statement.charAt(p) == '(') {
                            int q = p + 1;
                            while (q < statement.length() && Character.isWhitespace(statement.charAt(q))) q++;
                            if (q < statement.length() && statement.charAt(q) == '0') {
                                q++;
                                while (q < statement.length() && Character.isWhitespace(statement.charAt(q))) q++;
                                if (q < statement.length() && statement.charAt(q) == ')') {
                                    output.append("DECIMAL");
                                    i = q + 1;
                                    continue;
                                }
                            }
                        }
                    }
                    if (inColumnList && parenDepth == 1 && word.equalsIgnoreCase("NUMBER")) {
                        output.append("NUMERIC");
                    } else {
                        output.append(word);
                    }
                    i = end;
                    continue;
                }
            } else if (single && ch == '\'') {
                if (next == '\'') { output.append(ch).append(next); i += 2; continue; }
                single = false;
            } else if (doubleQuote && ch == '"') {
                if (next == '"') { output.append(ch).append(next); i += 2; continue; }
                doubleQuote = false;
            }
            else if (backtick && ch == '`') backtick = false;
            else if (bracket && ch == ']') bracket = false;
            output.append(ch);
            i++;
        }
        return output.toString();
    }
}
