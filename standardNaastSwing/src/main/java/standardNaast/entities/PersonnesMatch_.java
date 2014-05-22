package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PersonnesMatch.class)
public class PersonnesMatch_ {

    public static volatile SingularAttribute<PersonnesMatch, PersonnesMatchPK> id;
    public static volatile SingularAttribute<PersonnesMatch, BigDecimal> paye;
    public static volatile SingularAttribute<PersonnesMatch, Match> match;
    public static volatile SingularAttribute<PersonnesMatch, PrixLocomotion> prixLocomotion;
}
