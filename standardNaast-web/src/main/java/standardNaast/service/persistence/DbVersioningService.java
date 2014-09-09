package standardNaast.service.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import standardNaast.exception.TechnicalException;
import standardNaast.utils.DbUtils;

/**
 * Description: database versioning service. Runs a schema/data upgrade at
 * application startup.
 *
 * @author BuyWay-Services: DWW<BR>
 *         Created on 29 août 2012
 */
@Startup
@Singleton
public class DbVersioningService {

	/** The LOG. */
	private static final Logger LOG = Logger
			.getLogger(DbVersioningService.class);

	/** The dataSource. */
	@Resource(lookup = "java:jboss/datasources/StandardNaastDS")
	private DataSource dataSource;

	/**
	 * Run the database schema upgrade.
	 */
	@PostConstruct
	public void initDb() {
		DbVersioningService.LOG.debug("********** Upgrading database...");
		final Connection connection = this.getConnection();
		DbUtils.runLiquibase(connection, "changelog.xml");
		DbVersioningService.LOG.debug("********** Database upgrade done...");
	}

	/**
	 * @return the jdbc connection
	 */
	private Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (final SQLException sqlException) {
			throw new TechnicalException(sqlException);
		}
	}
}
