/* Copyright 2012 BuyWay-Services */
package standardNaast.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.util.file.FilenameUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.exception.TechnicalException;

/**
 * Description: Database utils.
 * 
 * @author BuyWay-Services: DWW<BR>
 *         Created on 12 sept. 2012
 */
public class DbUtils {

	private static final Logger LOG = LogManager.getLogger(DbUtils.class);

	private static final String CHANGELOG = "META-INF/changelog.xml";

	/**
	 * Run liquibase with given connection. This is static public in order to be
	 * callable from InitializeTestData.
	 * 
	 * @param connection
	 *            the connection
	 * @param changelog
	 *            the changelog referencing the child changelog folder
	 */
	public static void runLiquibase() {
		try {
			final Database database = new H2Database();
			final JdbcConnection jdbcConnection = new JdbcConnection(
					DbUtils.getConnection());
			database.setConnection(jdbcConnection);

			// resource accessor
			final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor() {

				@Override
				public Set<String> list(String relativeTo, final String path,
						final boolean includeFiles,
						final boolean includeDirectories,
						final boolean recursive) throws IOException {
					relativeTo = FilenameUtils.getFullPath(relativeTo);
					return super.list(relativeTo, path, includeFiles,
							includeDirectories, recursive);
				}
			};
			// run the changelogs
			final Liquibase liquibase = new Liquibase(DbUtils.CHANGELOG,
					resourceAccessor, database);
			liquibase.update("");
			jdbcConnection.commit();
			jdbcConnection.close();

		} catch (final LiquibaseException liquibaseException) {
			throw new TechnicalException(liquibaseException);
		}
	}

	private static final Connection getConnection() {
		try {
			Class.forName("org.h2.Driver");
			final Connection conn = DriverManager.getConnection(
					"jdbc:h2:file:../database/h2/dbs/standard_naast", "sa", "");
			return conn;
		} catch (ClassNotFoundException | SQLException e) {
			DbUtils.LOG.error(
					"Unable to acquire database connection due to : ", e);
			throw new RuntimeException();
		}
	}
}
