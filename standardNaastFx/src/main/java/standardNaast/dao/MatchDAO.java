package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Match;
import standardNaast.entities.Season;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;

public interface MatchDAO {

	List<Match> getMatchListWithMatchType(Season season, CompetitionType competitiontype, MatchType matchType);

	List<Match> getMatchList(Season season, CompetitionType competitiontype);

	Match addMatch(Match match);

	Match getMatch(long id);

	List<Match> getMatchListWithPlaceType(Season season, CompetitionType competitiontype, Place place);

}
