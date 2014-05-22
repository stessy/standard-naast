package standardNaast.entities;

import java.math.BigDecimal;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Bloc.class)
public class Bloc_ {

    public static volatile SingularAttribute<Bloc, Long> blocId;
    public static volatile SingularAttribute<Bloc, String> blocValue;
    public static volatile SingularAttribute<Bloc, String> club;
    public static volatile SingularAttribute<Bloc, BigDecimal> equipeId;
    public static volatile SingularAttribute<Bloc, String> tribune;
    public static volatile ListAttribute<Bloc, CommandePlace> commandePlaces;
}
