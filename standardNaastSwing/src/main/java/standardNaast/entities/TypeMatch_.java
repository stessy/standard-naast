package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TypeMatch.class)
public class TypeMatch_ {

    public static volatile SingularAttribute<TypeMatch, Long> typeMatchId;
    public static volatile SingularAttribute<TypeMatch, String> denominationMatch;
    public static volatile SingularAttribute<TypeMatch, BigDecimal> typeCompetitionId;
    public static volatile ListAttribute<TypeMatch, Match> matches;
    public static volatile ListAttribute<TypeMatch, PrixPlace> prixPlaces;
}
