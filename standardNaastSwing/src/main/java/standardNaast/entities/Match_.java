package standardNaast.entities;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Match.class)
public class Match_ {

    public static volatile SingularAttribute<Match, Long> matchId;
    public static volatile SingularAttribute<Match, Date> dateMatch;
    public static volatile SingularAttribute<Match, String> lieu;
    public static volatile ListAttribute<Match, CommandePlace> commandePlaces;
    public static volatile SingularAttribute<Match, SaisonEquipe> saisonEquipe;
    public static volatile SingularAttribute<Match, TypeMatch> typeMatchBean;
    public static volatile ListAttribute<Match, PersonnesMatch> personnesMatches;
}
