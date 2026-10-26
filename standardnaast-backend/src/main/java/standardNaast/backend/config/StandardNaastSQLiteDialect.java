package standardNaast.backend.config;

import org.hibernate.community.dialect.SQLiteDialect;

import java.sql.Types;

public class StandardNaastSQLiteDialect extends SQLiteDialect {
    @Override
    public boolean equivalentTypes(int first, int second) {
        // SQLite's INTEGER storage class is signed 64-bit, including INTEGER PRIMARY KEY.
        return super.equivalentTypes(first, second) || (isInteger(first) && isInteger(second));
    }

    private static boolean isInteger(int type) {
        return type == Types.INTEGER || type == Types.BIGINT || type == Types.SMALLINT || type == Types.TINYINT;
    }
}
