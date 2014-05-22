package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Benevolat.class)
public class Benevolat_ {

    public static volatile SingularAttribute<Benevolat, Long> id;
    public static volatile SingularAttribute<Benevolat, Date> dateBenevolat;
    public static volatile SingularAttribute<Benevolat, BigDecimal> amount;
    public static volatile SingularAttribute<Benevolat, String> typeBenevolat;
    public static volatile SingularAttribute<Benevolat, Personne> personne;
}
