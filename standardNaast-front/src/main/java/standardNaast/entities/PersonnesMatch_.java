package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-29T09:07:47.557+0200")
@StaticMetamodel(PersonnesMatch.class)
public class PersonnesMatch_ {
	public static volatile SingularAttribute<PersonnesMatch, Personne> personne;
	public static volatile SingularAttribute<PersonnesMatch, PersonnesMatchPK> id;
	public static volatile SingularAttribute<PersonnesMatch, Match> match;
	public static volatile SingularAttribute<PersonnesMatch, Integer> carTravelAmount;
}