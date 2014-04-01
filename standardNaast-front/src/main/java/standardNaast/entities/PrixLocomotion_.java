package standardNaast.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.968+0100")
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
	public static volatile ListAttribute<PrixLocomotion, PersonnesMatch> personnesMatches;
}
