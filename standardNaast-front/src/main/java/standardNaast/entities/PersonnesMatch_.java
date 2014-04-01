package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.962+0100")
@StaticMetamodel(PersonnesMatch.class)
public class PersonnesMatch_ {
	public static volatile SingularAttribute<PersonnesMatch, Boolean> paye;
	public static volatile SingularAttribute<PersonnesMatch, Personne> personne;
	public static volatile SingularAttribute<PersonnesMatch, PersonnesMatchPK> id;
	public static volatile SingularAttribute<PersonnesMatch, Match> match;
	public static volatile SingularAttribute<PersonnesMatch, PrixLocomotion> prixLocomotion;
}
