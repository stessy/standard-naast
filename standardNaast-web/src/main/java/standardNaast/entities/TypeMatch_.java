package standardNaast.entities;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.514+0200")
@StaticMetamodel(TypeMatch.class)
public class TypeMatch_ {
	public static volatile SingularAttribute<TypeMatch, BigInteger> typeCompetitionId;
	public static volatile SingularAttribute<TypeMatch, Long> typeMatchId;
	public static volatile SingularAttribute<TypeMatch, String> denominationMatch;
	public static volatile ListAttribute<TypeMatch, Match> matches;
	public static volatile ListAttribute<TypeMatch, PrixPlace> prixPlaces;
}