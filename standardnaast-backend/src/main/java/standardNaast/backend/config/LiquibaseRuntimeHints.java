package standardNaast.backend.config;

import liquibase.change.AddColumnConfig;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.change.core.CreateSequenceChange;
import liquibase.change.core.CreateTableChange;
import liquibase.change.core.SQLFileChange;
import liquibase.precondition.core.ColumnExistsPrecondition;
import liquibase.precondition.core.NotPrecondition;
import liquibase.precondition.core.SequenceExistsPrecondition;
import liquibase.precondition.core.TableExistsPrecondition;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class LiquibaseRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("db/changelog/**");
        for (Class<?> type : new Class<?>[]{
                SQLFileChange.class, CreateSequenceChange.class, CreateTableChange.class,
                AddPrimaryKeyChange.class, AddColumnChange.class, ColumnConfig.class,
                AddColumnConfig.class, ConstraintsConfig.class, NotPrecondition.class,
                SequenceExistsPrecondition.class, TableExistsPrecondition.class, ColumnExistsPrecondition.class}) {
            // Liquibase sets XML attributes reflectively, including inherited setters.
            for (Class<?> current = type; current != Object.class; current = current.getSuperclass()) {
                hints.reflection().registerType(current, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_DECLARED_METHODS);
            }
        }
    }
}
