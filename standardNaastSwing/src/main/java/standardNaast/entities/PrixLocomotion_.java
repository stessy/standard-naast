package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PrixLocomotion.class)
public class PrixLocomotion_ {

    public static volatile SingularAttribute<PrixLocomotion, Long> prixLocomotionId;
    public static volatile SingularAttribute<PrixLocomotion, BigDecimal> ageMaximum;
    public static volatile SingularAttribute<PrixLocomotion, BigDecimal> ageMinimum;
    public static volatile SingularAttribute<PrixLocomotion, String> annee;
    public static volatile SingularAttribute<PrixLocomotion, String> lieu;
    public static volatile SingularAttribute<PrixLocomotion, BigDecimal> membre;
    public static volatile SingularAttribute<PrixLocomotion, BigDecimal> montant;
    public static volatile SingularAttribute<PrixLocomotion, String> typePersonne;
    public static volatile ListAttribute<PrixLocomotion, PersonnesMatch> personnesMatches;
}
