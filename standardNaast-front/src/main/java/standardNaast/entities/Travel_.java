package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import types.Place;

@Generated(value="Dali", date="2014-05-16T21:24:37.504+0200")
@StaticMetamodel(Travel.class)
public class Travel_ {
	public static volatile SingularAttribute<Travel, Date> annee;
	public static volatile SingularAttribute<Travel, BigDecimal> montant;
	public static volatile SingularAttribute<Travel, Place> place;
	public static volatile SingularAttribute<Travel, Boolean> membre;
	public static volatile SingularAttribute<Travel, Integer> ageMinimum;
	public static volatile SingularAttribute<Travel, Integer> ageMaximum;
	public static volatile SingularAttribute<Travel, Long> id;
	public static volatile SingularAttribute<Travel, String> typePersonne;
}
