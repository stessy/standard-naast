package standardNaast.producers;

//import java.sql.SQLException;

//import javax.annotation.Resource;
//import javax.enterprise.inject.Produces;
//import javax.sql.DataSource;
//
//import liquibase.integration.cdi.CDILiquibaseConfig;
//import liquibase.integration.cdi.annotations.LiquibaseType;
//import liquibase.resource.ClassLoaderResourceAccessor;
//import liquibase.resource.ResourceAccessor;

/**
 * A Simple CDI Producer to configure the CDI Liquibase integration
 *
 */
public class LiquibaseProducer {

	// @Resource(lookup = "jdbc/standardNaast")
//	@Resource(lookup = "java:jboss/datasources/StandardNaastDS")
//	private DataSource myDataSource;
//
//	@Produces
//	@LiquibaseType
//	public CDILiquibaseConfig createConfig() {
//		final CDILiquibaseConfig config = new CDILiquibaseConfig();
//		config.setChangeLog("changelog.xml");
//		return config;
//	}
//
//	@Produces
//	@LiquibaseType
//	public DataSource createDataSource() throws SQLException {
//		return this.myDataSource;
//	}
//
//	@Produces
//	@LiquibaseType
//	public ResourceAccessor create() {
//		return new ClassLoaderResourceAccessor(this.getClass().getClassLoader());
//	}

}