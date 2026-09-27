package standardNaast.backend.migration;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Moteur complet et autonome de migration de base de données H2 vers PostgreSQL.
 * Lit les schémas, tables, colonnes, contraintes (PK, FK, Unique), séquences et données
 * depuis une base H2 existante (.mv.db) et les reproduit fidèlement dans PostgreSQL.
 */
public class H2ToPostgresMigrator {

    public interface MigrationProgressListener {
        void onProgress(String message, double percent);
    }

    public static class MigrationConfig {
        private String h2PathOrUrl;
        private String h2User = "sa";
        private String h2Password = "";
        private String pgUrl = "jdbc:postgresql://localhost:5432/standardnaast";
        private String pgUser = "standardnaast";
        private String pgPassword = "password";
        private boolean dropTargetTablesIfExists = false;
        private boolean truncateTargetTables = false;
        private int batchSize = 1000;
        private MigrationProgressListener listener;

        public MigrationConfig() {}

        public MigrationConfig(String h2PathOrUrl) {
            this.h2PathOrUrl = h2PathOrUrl;
        }

        public String getH2PathOrUrl() { return h2PathOrUrl; }
        public MigrationConfig setH2PathOrUrl(String h2PathOrUrl) { this.h2PathOrUrl = h2PathOrUrl; return this; }
        public String getH2User() { return h2User; }
        public MigrationConfig setH2User(String h2User) { this.h2User = h2User; return this; }
        public String getH2Password() { return h2Password; }
        public MigrationConfig setH2Password(String h2Password) { this.h2Password = h2Password; return this; }
        public String getPgUrl() { return pgUrl; }
        public MigrationConfig setPgUrl(String pgUrl) { this.pgUrl = pgUrl; return this; }
        public String getPgUser() { return pgUser; }
        public MigrationConfig setPgUser(String pgUser) { this.pgUser = pgUser; return this; }
        public String getPgPassword() { return pgPassword; }
        public MigrationConfig setPgPassword(String pgPassword) { this.pgPassword = pgPassword; return this; }
        public boolean isDropTargetTablesIfExists() { return dropTargetTablesIfExists; }
        public MigrationConfig setDropTargetTablesIfExists(boolean dropTargetTablesIfExists) { this.dropTargetTablesIfExists = dropTargetTablesIfExists; return this; }
        public boolean isTruncateTargetTables() { return truncateTargetTables; }
        public MigrationConfig setTruncateTargetTables(boolean truncateTargetTables) { this.truncateTargetTables = truncateTargetTables; return this; }
        public int getBatchSize() { return batchSize; }
        public MigrationConfig setBatchSize(int batchSize) { this.batchSize = batchSize; return this; }
        public MigrationProgressListener getListener() { return listener; }
        public MigrationConfig setListener(MigrationProgressListener listener) { this.listener = listener; return this; }
    }

    public static class MigrationResult {
        private boolean success;
        private String message;
        private int tablesCreated;
        private int sequencesCreated;
        private long totalRowsMigrated;
        private final Map<String, Long> rowsPerTable = new LinkedHashMap<>();
        private final List<String> warnings = new ArrayList<>();
        private long durationMillis;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public int getTablesCreated() { return tablesCreated; }
        public void setTablesCreated(int tablesCreated) { this.tablesCreated = tablesCreated; }
        public int getSequencesCreated() { return sequencesCreated; }
        public void setSequencesCreated(int sequencesCreated) { this.sequencesCreated = sequencesCreated; }
        public long getTotalRowsMigrated() { return totalRowsMigrated; }
        public void setTotalRowsMigrated(long totalRowsMigrated) { this.totalRowsMigrated = totalRowsMigrated; }
        public Map<String, Long> getRowsPerTable() { return rowsPerTable; }
        public List<String> getWarnings() { return warnings; }
        public long getDurationMillis() { return durationMillis; }
        public void setDurationMillis(long durationMillis) { this.durationMillis = durationMillis; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Migration Result ===\n");
            sb.append("Statut: ").append(success ? "SUCCÈS" : "ÉCHEC").append("\n");
            sb.append("Message: ").append(message).append("\n");
            sb.append("Durée: ").append(durationMillis).append(" ms\n");
            sb.append("Séquences créées/mises à jour: ").append(sequencesCreated).append("\n");
            sb.append("Tables migrées: ").append(rowsPerTable.size()).append("\n");
            sb.append("Total lignes migrées: ").append(totalRowsMigrated).append("\n");
            sb.append("Détails par table:\n");
            for (Map.Entry<String, Long> entry : rowsPerTable.entrySet()) {
                sb.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" lignes\n");
            }
            if (!warnings.isEmpty()) {
                sb.append("Avertissements (").append(warnings.size()).append("):\n");
                for (String w : warnings) {
                    sb.append("  ! ").append(w).append("\n");
                }
            }
            return sb.toString();
        }
    }

    private static class ColumnInfo {
        String name;
        int dataType;
        String typeName;
        int columnSize;
        int decimalDigits;
        boolean nullable;
        String defaultValue;
    }

    private static class ForeignKeyInfo {
        String fkName;
        String pkTableName;
        String pkColumnName;
        String fkTableName;
        String fkColumnName;
    }

    private static class SequenceInfo {
        String name;
        long currentValue;
        long increment;
    }

    /**
     * Point d'entrée principal pour exécuter la migration.
     */
    public MigrationResult migrate(MigrationConfig config) {
        long startTime = System.currentTimeMillis();
        MigrationResult result = new MigrationResult();

        notify(config, "Démarrage de la migration H2 vers PostgreSQL...", 0.0);

        String h2JdbcUrl = normalizeH2Url(config.getH2PathOrUrl());
        System.out.println("[MIGRATION] URL H2 Source: " + h2JdbcUrl);
        System.out.println("[MIGRATION] URL PostgreSQL Cible: " + config.getPgUrl());

        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            result.setSuccess(false);
            result.setMessage("Driver H2 non trouvé: " + e.getMessage());
            return result;
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            result.setSuccess(false);
            result.setMessage("Driver PostgreSQL non trouvé: " + e.getMessage());
            return result;
        }

        try (Connection h2Conn = DriverManager.getConnection(h2JdbcUrl, config.getH2User(), config.getH2Password());
             Connection pgConn = DriverManager.getConnection(config.getPgUrl(), config.getPgUser(), config.getPgPassword())) {

            pgConn.setAutoCommit(false);

            notify(config, "Connexions établies. Analyse du schéma H2...", 5.0);

            // 1. Lire les séquences H2
            List<SequenceInfo> sequences = extractSequences(h2Conn, result);
            notify(config, "Création des séquences (" + sequences.size() + " trouvées)...", 10.0);
            createSequencesInPostgres(pgConn, sequences, result);
            result.setSequencesCreated(sequences.size());

            // 2. Extraire la liste des tables
            List<String> tableNames = extractUserTables(h2Conn);
            notify(config, "Extraction de " + tableNames.size() + " tables...", 15.0);

            Map<String, List<ColumnInfo>> tableColumns = new LinkedHashMap<>();
            Map<String, List<String>> tablePrimaryKeys = new LinkedHashMap<>();
            List<ForeignKeyInfo> foreignKeys = new ArrayList<>();

            for (String tableName : tableNames) {
                tableColumns.put(tableName, extractColumns(h2Conn, tableName));
                tablePrimaryKeys.put(tableName, extractPrimaryKeys(h2Conn, tableName));
                foreignKeys.addAll(extractForeignKeys(h2Conn, tableName));
            }

            // 3. Création des tables dans PostgreSQL (sans contraintes FK pour l'insertion ordonnée)
            notify(config, "Création des tables dans PostgreSQL...", 20.0);
            for (String tableName : tableNames) {
                createTableInPostgres(pgConn, tableName, tableColumns.get(tableName), tablePrimaryKeys.get(tableName), config.isDropTargetTablesIfExists());
            }
            pgConn.commit();
            result.setTablesCreated(tableNames.size());

            // 4. Copie des données table par table
            long totalRows = 0;
            double progressStep = 60.0 / Math.max(1, tableNames.size());
            double currentProgress = 25.0;

            for (String tableName : tableNames) {
                notify(config, "Migration des données de la table " + tableName + "...", currentProgress);
                long rowsCopied = copyTableData(h2Conn, pgConn, tableName, tableColumns.get(tableName), config);
                result.getRowsPerTable().put(tableName, rowsCopied);
                totalRows += rowsCopied;
                currentProgress += progressStep;
                pgConn.commit();
            }
            result.setTotalRowsMigrated(totalRows);

            // 5. Application des contraintes de clés étrangères
            notify(config, "Application des contraintes et clés étrangères...", 85.0);
            applyForeignKeysInPostgres(pgConn, foreignKeys, result);
            pgConn.commit();

            // 6. Synchronisation et mise à jour des compteurs de séquences PostgreSQL
            notify(config, "Synchronisation des séquences...", 92.0);
            syncPostgresSequences(pgConn, sequences, tablePrimaryKeys, result);
            pgConn.commit();

            result.setSuccess(true);
            result.setMessage("Migration terminée avec succès.");
            notify(config, "Migration réussie (" + totalRows + " enregistrements transférés).", 100.0);

        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("Erreur lors de la migration: " + e.getMessage());
            notify(config, "Erreur migration: " + e.getMessage(), -1.0);
        } finally {
            result.setDurationMillis(System.currentTimeMillis() - startTime);
        }

        return result;
    }

    private void notify(MigrationConfig config, String message, double percent) {
        System.out.println("[MIGRATION] " + String.format("%.0f%%", Math.max(0, percent)) + " - " + message);
        if (config.getListener() != null) {
            config.getListener().onProgress(message, percent);
        }
    }

    private String normalizeH2Url(String rawPathOrUrl) {
        if (rawPathOrUrl == null || rawPathOrUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin ou l'URL de la base H2 ne peut pas être vide.");
        }
        String path = rawPathOrUrl.trim();
        if (path.startsWith("jdbc:h2:")) {
            return path;
        }

        // Si l'utilisateur donne un chemin absolu ou relatif vers un fichier standardnaast.mv.db
        if (path.endsWith(".mv.db")) {
            path = path.substring(0, path.length() - ".mv.db".length());
        } else if (path.endsWith(".h2.db")) {
            path = path.substring(0, path.length() - ".h2.db".length());
        }

        File file = new File(path);
        String absolutePath = file.getAbsolutePath();

        return "jdbc:h2:file:" + absolutePath + ";DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE;NON_KEYWORDS=VALUE,USER,ROLE";
    }

    private List<String> extractUserTables(Connection h2Conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        DatabaseMetaData meta = h2Conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, "PUBLIC", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (tableName != null && !tableName.startsWith("DATABASECHANGELOG")) {
                    tables.add(tableName);
                }
            }
        }
        return tables;
    }

    private List<ColumnInfo> extractColumns(Connection h2Conn, String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        DatabaseMetaData meta = h2Conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, "PUBLIC", tableName, "%")) {
            while (rs.next()) {
                ColumnInfo col = new ColumnInfo();
                col.name = rs.getString("COLUMN_NAME");
                col.dataType = rs.getInt("DATA_TYPE");
                col.typeName = rs.getString("TYPE_NAME");
                col.columnSize = rs.getInt("COLUMN_SIZE");
                col.decimalDigits = rs.getInt("DECIMAL_DIGITS");
                col.nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                col.defaultValue = rs.getString("COLUMN_DEF");
                columns.add(col);
            }
        }
        return columns;
    }

    private List<String> extractPrimaryKeys(Connection h2Conn, String tableName) throws SQLException {
        List<String> pkCols = new ArrayList<>();
        DatabaseMetaData meta = h2Conn.getMetaData();
        try (ResultSet rs = meta.getPrimaryKeys(null, "PUBLIC", tableName)) {
            while (rs.next()) {
                pkCols.add(rs.getString("COLUMN_NAME"));
            }
        }
        return pkCols;
    }

    private List<ForeignKeyInfo> extractForeignKeys(Connection h2Conn, String tableName) throws SQLException {
        List<ForeignKeyInfo> fks = new ArrayList<>();
        DatabaseMetaData meta = h2Conn.getMetaData();
        try (ResultSet rs = meta.getImportedKeys(null, "PUBLIC", tableName)) {
            while (rs.next()) {
                ForeignKeyInfo fk = new ForeignKeyInfo();
                fk.fkName = rs.getString("FK_NAME");
                fk.pkTableName = rs.getString("PKTABLE_NAME");
                fk.pkColumnName = rs.getString("PKCOLUMN_NAME");
                fk.fkTableName = rs.getString("FKTABLE_NAME");
                fk.fkColumnName = rs.getString("FKCOLUMN_NAME");
                if (fk.pkTableName != null && !fk.pkTableName.startsWith("DATABASECHANGELOG")) {
                    fks.add(fk);
                }
            }
        }
        return fks;
    }

    private List<SequenceInfo> extractSequences(Connection h2Conn, MigrationResult result) {
        List<SequenceInfo> sequences = new ArrayList<>();
        String sql = "SELECT * FROM INFORMATION_SCHEMA.SEQUENCES WHERE SEQUENCE_SCHEMA = 'PUBLIC'";
        try (Statement stmt = h2Conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData rsMeta = rs.getMetaData();
            boolean hasCurrentVal = false;
            boolean hasStartVal = false;
            boolean hasIncrement = false;
            for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                String col = rsMeta.getColumnName(i).toUpperCase();
                if ("CURRENT_VALUE".equals(col)) hasCurrentVal = true;
                if ("START_VALUE".equals(col)) hasStartVal = true;
                if ("INCREMENT".equals(col)) hasIncrement = true;
            }

            while (rs.next()) {
                String name = rs.getString("SEQUENCE_NAME");
                if (name != null && !name.toUpperCase().startsWith("SYSTEM_SEQUENCE")) {
                    SequenceInfo seq = new SequenceInfo();
                    seq.name = name;
                    long val = 1;
                    if (hasCurrentVal) {
                        try { val = rs.getLong("CURRENT_VALUE"); } catch (Exception ignored) {}
                    } else if (hasStartVal) {
                        try { val = rs.getLong("START_VALUE"); } catch (Exception ignored) {}
                    }
                    long inc = 1;
                    if (hasIncrement) {
                        try { inc = rs.getLong("INCREMENT"); } catch (Exception ignored) {}
                    }
                    seq.currentValue = val;
                    seq.increment = inc != 0 ? inc : 1;
                    sequences.add(seq);
                }
            }
        } catch (SQLException e) {
            result.getWarnings().add("Impossible de lister les séquences via INFORMATION_SCHEMA: " + e.getMessage());
        }
        return sequences;
    }

    private void createSequencesInPostgres(Connection pgConn, List<SequenceInfo> sequences, MigrationResult result) throws SQLException {
        for (SequenceInfo seq : sequences) {
            String checkSql = "SELECT 1 FROM pg_sequences WHERE schemaname = 'public' AND sequencename = lower(?)";
            boolean exists = false;
            try (PreparedStatement ps = pgConn.prepareStatement(checkSql)) {
                ps.setString(1, seq.name);
                try (ResultSet rs = ps.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (!exists) {
                long startVal = Math.max(1, seq.currentValue);
                long inc = seq.increment != 0 ? seq.increment : 1;
                String ddl = String.format("CREATE SEQUENCE IF NOT EXISTS %s START WITH %d INCREMENT BY %d",
                        escapeIdentifier(seq.name), startVal, inc);
                try (Statement stmt = pgConn.createStatement()) {
                    stmt.execute(ddl);
                }
            }
        }
    }

    private void createTableInPostgres(Connection pgConn, String tableName, List<ColumnInfo> columns, List<String> primaryKeys, boolean dropIfExists) throws SQLException {
        if (dropIfExists) {
            try (Statement stmt = pgConn.createStatement()) {
                stmt.execute("DROP TABLE IF EXISTS " + escapeIdentifier(tableName) + " CASCADE");
            }
        }

        StringBuilder ddl = new StringBuilder();
        ddl.append("CREATE TABLE IF NOT EXISTS ").append(escapeIdentifier(tableName)).append(" (\n");

        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo col = columns.get(i);
            ddl.append("    ").append(escapeIdentifier(col.name)).append(" ")
               .append(mapH2TypeToPostgres(col));

            if (!col.nullable) {
                ddl.append(" NOT NULL");
            }
            if (col.defaultValue != null && !col.defaultValue.trim().isEmpty() && !col.defaultValue.toUpperCase().contains("NEXT VALUE")) {
                ddl.append(" DEFAULT ").append(col.defaultValue);
            }
            if (i < columns.size() - 1 || (primaryKeys != null && !primaryKeys.isEmpty())) {
                ddl.append(",\n");
            }
        }

        if (primaryKeys != null && !primaryKeys.isEmpty()) {
            ddl.append("    CONSTRAINT PK_").append(tableName.replaceAll("[^a-zA-Z0-9_]", ""))
               .append(" PRIMARY KEY (");
            for (int i = 0; i < primaryKeys.size(); i++) {
                ddl.append(escapeIdentifier(primaryKeys.get(i)));
                if (i < primaryKeys.size() - 1) ddl.append(", ");
            }
            ddl.append(")\n");
        }

        ddl.append(")");

        try (Statement stmt = pgConn.createStatement()) {
            stmt.execute(ddl.toString());
        }
    }

    private String mapH2TypeToPostgres(ColumnInfo col) {
        String typeUpper = col.typeName != null ? col.typeName.toUpperCase() : "";

        if (typeUpper.contains("VARCHAR") || typeUpper.contains("CHARACTER VARYING")) {
            return col.columnSize > 0 ? "VARCHAR(" + col.columnSize + ")" : "VARCHAR(255)";
        }
        if (typeUpper.equals("CHAR") || typeUpper.equals("CHARACTER")) {
            return "CHAR(" + Math.max(1, col.columnSize) + ")";
        }
        if (typeUpper.contains("CLOB") || typeUpper.contains("TEXT") || typeUpper.contains("LONGVARCHAR")) {
            return "TEXT";
        }
        if (typeUpper.contains("BIGINT") || typeUpper.contains("IDENTITY") || col.dataType == Types.BIGINT) {
            return "BIGINT";
        }
        if (typeUpper.contains("INT8")) {
            return "BIGINT";
        }
        if (typeUpper.contains("INT4") || typeUpper.equals("INT") || typeUpper.equals("INTEGER") || typeUpper.equals("MEDIUMINT") || col.dataType == Types.INTEGER) {
            return "INTEGER";
        }
        if (typeUpper.contains("INT2") || typeUpper.equals("SMALLINT") || typeUpper.equals("TINYINT") || col.dataType == Types.SMALLINT || col.dataType == Types.TINYINT) {
            return "SMALLINT";
        }
        if (typeUpper.contains("BOOL") || typeUpper.contains("BIT") || col.dataType == Types.BOOLEAN || col.dataType == Types.BIT) {
            return "BOOLEAN";
        }
        if (typeUpper.contains("DATE") || col.dataType == Types.DATE) {
            return "DATE";
        }
        if (typeUpper.contains("TIMESTAMP") || typeUpper.contains("DATETIME") || col.dataType == Types.TIMESTAMP) {
            return "TIMESTAMP";
        }
        if (typeUpper.contains("DECIMAL") || typeUpper.contains("NUMERIC") || col.dataType == Types.NUMERIC || col.dataType == Types.DECIMAL) {
            if (col.columnSize > 0) {
                return "NUMERIC(" + col.columnSize + ", " + Math.max(0, col.decimalDigits) + ")";
            }
            return "NUMERIC(19, 2)";
        }
        if (typeUpper.contains("DOUBLE") || typeUpper.contains("FLOAT8") || col.dataType == Types.DOUBLE || col.dataType == Types.FLOAT) {
            return "DOUBLE PRECISION";
        }
        if (typeUpper.contains("REAL") || typeUpper.contains("FLOAT4") || col.dataType == Types.REAL) {
            return "REAL";
        }
        if (typeUpper.contains("BLOB") || typeUpper.contains("BINARY") || typeUpper.contains("BYTEA") || col.dataType == Types.BLOB || col.dataType == Types.VARBINARY) {
            return "BYTEA";
        }

        return "VARCHAR(255)";
    }

    private long copyTableData(Connection h2Conn, Connection pgConn, String tableName, List<ColumnInfo> columns, MigrationConfig config) throws SQLException {
        if (config.isTruncateTargetTables()) {
            try (Statement stmt = pgConn.createStatement()) {
                stmt.execute("TRUNCATE TABLE " + escapeIdentifier(tableName) + " CASCADE");
            } catch (SQLException ignored) {}
        }

        StringBuilder selectSql = new StringBuilder("SELECT ");
        StringBuilder insertSql = new StringBuilder("INSERT INTO ").append(escapeIdentifier(tableName)).append(" (");
        StringBuilder valuesSql = new StringBuilder(" VALUES (");

        for (int i = 0; i < columns.size(); i++) {
            String colName = escapeIdentifier(columns.get(i).name);
            selectSql.append(colName);
            insertSql.append(colName);
            valuesSql.append("?");
            if (i < columns.size() - 1) {
                selectSql.append(", ");
                insertSql.append(", ");
                valuesSql.append(", ");
            }
        }
        selectSql.append(" FROM ").append(escapeIdentifier(tableName));
        insertSql.append(")").append(valuesSql).append(")");

        // Pour éviter les collisions d'insertion si des données existent déjà
        insertSql.append(" ON CONFLICT DO NOTHING");

        long rowsCount = 0;
        try (Statement selectStmt = h2Conn.createStatement();
             ResultSet rs = selectStmt.executeQuery(selectSql.toString());
             PreparedStatement insertStmt = pgConn.prepareStatement(insertSql.toString())) {

            int batchCount = 0;
            while (rs.next()) {
                for (int i = 0; i < columns.size(); i++) {
                    Object val = rs.getObject(i + 1);
                    if (val == null) {
                        insertStmt.setNull(i + 1, columns.get(i).dataType);
                    } else {
                        // Casts et ajustements de type
                        int targetType = columns.get(i).dataType;
                        if (targetType == Types.BOOLEAN || val instanceof Boolean) {
                            if (val instanceof Number) {
                                insertStmt.setBoolean(i + 1, ((Number) val).intValue() != 0);
                            } else if (val instanceof String) {
                                String str = ((String) val).trim();
                                insertStmt.setBoolean(i + 1, "true".equalsIgnoreCase(str) || "1".equals(str) || "t".equalsIgnoreCase(str) || "y".equalsIgnoreCase(str));
                            } else {
                                insertStmt.setBoolean(i + 1, rs.getBoolean(i + 1));
                            }
                        } else if (targetType == Types.DATE || val instanceof java.sql.Date) {
                            insertStmt.setDate(i + 1, rs.getDate(i + 1));
                        } else if (targetType == Types.TIMESTAMP || val instanceof java.sql.Timestamp) {
                            insertStmt.setTimestamp(i + 1, rs.getTimestamp(i + 1));
                        } else {
                            insertStmt.setObject(i + 1, val);
                        }
                    }
                }

                insertStmt.addBatch();
                rowsCount++;
                batchCount++;

                if (batchCount >= config.getBatchSize()) {
                    insertStmt.executeBatch();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) {
                insertStmt.executeBatch();
            }
        }

        return rowsCount;
    }

    private void applyForeignKeysInPostgres(Connection pgConn, List<ForeignKeyInfo> foreignKeys, MigrationResult result) {
        Set<String> processedFks = new HashSet<>();
        for (ForeignKeyInfo fk : foreignKeys) {
            String fkKey = fk.fkTableName + "_" + fk.fkColumnName + "_" + fk.pkTableName;
            if (processedFks.contains(fkKey)) continue;
            processedFks.add(fkKey);

            String constraintName = fk.fkName != null && !fk.fkName.trim().isEmpty()
                    ? fk.fkName
                    : "FK_" + fk.fkTableName + "_" + fk.fkColumnName;

            String sql = String.format("ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s)",
                    escapeIdentifier(fk.fkTableName),
                    escapeIdentifier(constraintName),
                    escapeIdentifier(fk.fkColumnName),
                    escapeIdentifier(fk.pkTableName),
                    escapeIdentifier(fk.pkColumnName));

            try (Statement stmt = pgConn.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                // Avertissement sans bloquer la migration
                result.getWarnings().add("Contrainte FK ignorée (" + constraintName + "): " + e.getMessage());
            }
        }
    }

    private void syncPostgresSequences(Connection pgConn, List<SequenceInfo> sequences, Map<String, List<String>> tablePrimaryKeys, MigrationResult result) {
        for (SequenceInfo seq : sequences) {
            try {
                // Essayer de trouver la table correspondante basée sur le nom de séquence
                String cleanSeq = seq.name.toUpperCase().replace("_SEQ", "").replace("SEQ_", "");
                String matchedTable = null;
                String pkCol = null;

                for (Map.Entry<String, List<String>> entry : tablePrimaryKeys.entrySet()) {
                    String tName = entry.getKey().toUpperCase();
                    if (tName.equals(cleanSeq) || tName.startsWith(cleanSeq) || cleanSeq.startsWith(tName)) {
                        matchedTable = entry.getKey();
                        if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                            pkCol = entry.getValue().get(0);
                        }
                        break;
                    }
                }

                long maxVal = seq.currentValue;
                if (matchedTable != null && pkCol != null) {
                    String query = "SELECT COALESCE(MAX(" + escapeIdentifier(pkCol) + "), 0) FROM " + escapeIdentifier(matchedTable);
                    try (Statement stmt = pgConn.createStatement();
                         ResultSet rs = stmt.executeQuery(query)) {
                        if (rs.next()) {
                            long tableMax = rs.getLong(1);
                            maxVal = Math.max(maxVal, tableMax);
                        }
                    } catch (SQLException ignored) {}
                }

                if (maxVal > 0) {
                    String setValSql = String.format("SELECT setval('%s', %d, true)", seq.name.toLowerCase(), maxVal);
                    try (Statement stmt = pgConn.createStatement()) {
                        stmt.execute(setValSql);
                    }
                }
            } catch (Exception e) {
                result.getWarnings().add("Impossible de synchroniser la séquence " + seq.name + ": " + e.getMessage());
            }
        }
    }

    private static String escapeIdentifier(String identifier) {
        if (identifier == null) return "";
        // Dans PostgreSQL, les identifiants en majuscules ou contenant des caractères spéciaux sont quotés
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    /**
     * Point d'entrée en ligne de commande pour lancer la migration manuellement.
     * Exemple:
     *   java standardNaast.backend.migration.H2ToPostgresMigrator --h2=/chemin/standardnaast.mv.db --pg-url=jdbc:postgresql://localhost:5432/standardnaast --pg-user=standardnaast --pg-password=password
     */
    public static void main(String[] args) {
        MigrationConfig config = new MigrationConfig();

        for (String arg : args) {
            if (arg.startsWith("--h2=")) {
                config.setH2PathOrUrl(arg.substring("--h2=".length()));
            } else if (arg.startsWith("--migrate-from-h2=")) {
                config.setH2PathOrUrl(arg.substring("--migrate-from-h2=".length()));
            } else if (arg.startsWith("--migrate-h2=")) {
                config.setH2PathOrUrl(arg.substring("--migrate-h2=".length()));
            } else if (arg.startsWith("--pg-url=")) {
                config.setPgUrl(arg.substring("--pg-url=".length()));
            } else if (arg.startsWith("--pg-user=")) {
                config.setPgUser(arg.substring("--pg-user=".length()));
            } else if (arg.startsWith("--pg-password=")) {
                config.setPgPassword(arg.substring("--pg-password=".length()));
            } else if (arg.equals("--drop-tables")) {
                config.setDropTargetTablesIfExists(true);
            } else if (arg.equals("--truncate-tables")) {
                config.setTruncateTargetTables(true);
            } else if (arg.equals("--help") || arg.equals("-h")) {
                System.out.println("Utilisation: H2ToPostgresMigrator --h2=<chemin_base_h2> [--pg-url=<url>] [--pg-user=<user>] [--pg-password=<pass>] [--drop-tables]");
                System.exit(0);
            }
        }

        if (config.getH2PathOrUrl() == null || config.getH2PathOrUrl().trim().isEmpty()) {
            System.err.println("ERREUR: Le paramètre --h2=<chemin> est obligatoire.");
            System.err.println("Exemple: java standardNaast.backend.migration.H2ToPostgresMigrator --h2=/path/to/standardnaast.mv.db");
            System.exit(1);
        }

        H2ToPostgresMigrator migrator = new H2ToPostgresMigrator();
        MigrationResult result = migrator.migrate(config);

        System.out.println(result);

        if (!result.isSuccess()) {
            System.exit(1);
        }
    }
}
