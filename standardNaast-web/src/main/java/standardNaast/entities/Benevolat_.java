package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.495+0200")
@StaticMetamodel(Benevolat.class)
public class Benevolat_ {
	public static volatile SingularAttribute<Benevolat, BigDecimal> amount;
	public static volatile SingularAttribute<Benevolat, Long> id;
	public static volatile SingularAttribute<Benevolat, Date> dateBenevolat;
	public static volatile SingularAttribute<Benevolat, String> typeBenevolat;
	public static volatile SingularAttribute<Benevolat, Personne> personne;
}
