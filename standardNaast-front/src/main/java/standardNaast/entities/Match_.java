package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import types.CompetitionType;
import types.MatchType;

@Generated(value="Dali", date="2014-04-20T08:19:24.227+0200")
@StaticMetamodel(Match.class)
public class Match_ {
	public static volatile SingularAttribute<Match, Long> id;
	public static volatile SingularAttribute<Match, Date> dateMatch;
	public static volatile SingularAttribute<Match, String> lieu;
	public static volatile ListAttribute<Match, CommandePlace> commandePlaces;
	public static volatile SingularAttribute<Match, Saison> saison;
	public static volatile SingularAttribute<Match, SaisonEquipe> saisonEquipe;
	public static volatile SingularAttribute<Match, TypeMatch> typeMatchBean;
	public static volatile ListAttribute<Match, PersonnesMatch> personnesMatches;
	public static volatile SingularAttribute<Match, CompetitionType> typeCompetition;
	public static volatile SingularAttribute<Match, MatchType> matchType;
	public static volatile SingularAttribute<Match, Team> opponent;
}
