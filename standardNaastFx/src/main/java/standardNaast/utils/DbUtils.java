/* Copyright 2012 BuyWay-Services */
package standardNaast.utils;

import java.io.IOException;
import java.sql.Connection;
import java.util.Set;

import javax.persistence.EntityManager;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.H2Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.util.file.FilenameUtils;
import standardNaast.exception.TechnicalException;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

/**
 * Description: Database utils.
 * 
 * @author BuyWay-Services: DWW<BR>
 *         Created on 12 sept. 2012
 */
public class DbUtils {

	private static final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

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
			DbUtils.entityManager.getTransaction().begin();
			final Connection connection = DbUtils.entityManager.unwrap(Connection.class);
			final Database database = new H2Database();
			final JdbcConnection jdbcConnection = new JdbcConnection(connection);
			database.setConnection(jdbcConnection);

			// resource accessor
			final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor() {

				@Override
				public Set<String> list(String relativeTo, final String path, final boolean includeFiles,
						final boolean includeDirectories, final boolean recursive)
						throws IOException {

					System.out.println(relativeTo);
					relativeTo = FilenameUtils.getFullPath(relativeTo);
					System.out.println(relativeTo);
					System.out.println(path);
					return super.list(relativeTo, path, includeFiles, includeDirectories, recursive);
				}
			};

			// run the changelogs
			final Liquibase liquibase = new Liquibase(DbUtils.CHANGELOG, resourceAccessor, database);
			liquibase.update("");
			DbUtils.entityManager.getTransaction().commit();
		} catch (final LiquibaseException liquibaseException) {
			throw new TechnicalException(liquibaseException);
		}
	}
}
