package standardNaast.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-20T08:36:59.684+0200")
@StaticMetamodel(PrixLocomotion.class)
public class PrixLocomotion_ {
	public static volatile SingularAttribute<PrixLocomotion, String> annee;
	public static volatile SingularAttribute<PrixLocomotion, BigDecimal> montant;
	public static volatile SingularAttribute<PrixLocomotion, String> lieu;
	public static volatile SingularAttribute<PrixLocomotion, Boolean> membre;
	public static volatile SingularAttribute<PrixLocomotion, Integer> ageMinimum;
	public static volatile SingularAttribute<PrixLocomotion, Integer> ageMaximum;
	public static volatile SingularAttribute<PrixLocomotion, Long> id;
	public static volatile SingularAttribute<PrixLocomotion, String> typePersonne;
}
