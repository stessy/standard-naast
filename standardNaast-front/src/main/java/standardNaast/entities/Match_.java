package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import types.CompetitionType;
import types.MatchType;
import types.Place;

@Generated(value="Dali", date="2014-05-12T09:15:03.660+0200")
@StaticMetamodel(Match.class)
public class Match_ {
	public static volatile SingularAttribute<Match, Long> id;
	public static volatile SingularAttribute<Match, Team> opponent;
	public static volatile SingularAttribute<Match, Date> dateMatch;
	public static volatile SingularAttribute<Match, Place> place;
	public static volatile ListAttribute<Match, CommandePlace> commandePlaces;
	public static volatile SingularAttribute<Match, Season> season;
	public static volatile SingularAttribute<Match, TypeMatch> typeMatch;
	public static volatile SingularAttribute<Match, CompetitionType> typeCompetition;
	public static volatile SingularAttribute<Match, MatchType> matchType;
	public static volatile ListAttribute<Match, PersonnesMatch> personnesMatches;
}
