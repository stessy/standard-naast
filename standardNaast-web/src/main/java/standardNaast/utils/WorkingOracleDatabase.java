package standardNaast.utils;

import liquibase.database.core.OracleDatabase;

/**
 * Description: Liquibase implementation of Database for Oracle is wrongly
 * reporting.
 * 
 */
public class WorkingOracleDatabase extends OracleDatabase {

	@Override
	public boolean supportsDropTableCascadeConstraints() {
		return true;
	}

}