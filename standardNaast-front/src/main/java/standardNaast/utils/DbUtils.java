/* Copyright 2012 BuyWay-Services */
package standardNaast.utils;

import java.sql.Connection;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import standardNaast.exception.TechnicalException;

/**
 * Description: Database utils.
 * 
 * @author BuyWay-Services: DWW<BR>
 *         Created on 12 sept. 2012
 */
public class DbUtils {

	/**
	 * Run liquibase with given connection. This is static public in order to be
	 * callable from InitializeTestData.
	 * 
	 * @param connection
	 *            the connection
	 * @param changelog
	 *            the changelog referencing the child changelog folder
	 */
	public static void runLiquibase(final Connection connection,
			final String changelog) {
		try {
			Database database = new HsqlDatabase();
			JdbcConnection jdbcConnection = new JdbcConnection(connection);
			database.setConnection(jdbcConnection);

			// resource accessor
			Thread currentThread = Thread.currentThread();
			ClassLoader contextClassLoader = currentThread
					.getContextClassLoader();
			ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(
					contextClassLoader);

			// run the changelogs
			Liquibase liquibase = new Liquibase(changelog, resourceAccessor,
					database);
			liquibase.update(null);
		} catch (LiquibaseException liquibaseException) {
			throw new TechnicalException(liquibaseException);
		}
	}
}
