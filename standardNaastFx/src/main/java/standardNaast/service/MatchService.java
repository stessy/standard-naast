package standardNaast.service;

import java.util.List;
import java.util.stream.Collectors;

import standardNaast.dao.MatchDAO;
import standardNaast.dao.MatchDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.dao.TeamDAO;
import standardNaast.dao.TeamDAOImpl;
import standardNaast.entities.Match;
import standardNaast.entities.Season;
import standardNaast.model.MatchModel;
import standardNaast.model.SeasonModel;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;

public class MatchService {

	private final MatchDAO matchDAO = new MatchDAOImpl();

	private final SeasonDAO seasonDAO = new SeasonDAOImpl();

	private final TeamDAO teamDAO = new TeamDAOImpl();

	public List<MatchModel> getMatchListForCompetitionWithMatchType(final SeasonModel seasonModel,
			final CompetitionType competitionType,
			final MatchType matchType) {
		final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
		final List<Match> matchList = this.matchDAO.getMatchListWithMatchType(season, competitionType, matchType);
		return matchList.stream().map(t -> MatchModel.toModel(t)).collect(Collectors.toList());
	}

	public List<MatchModel> getMatchListForCompetitionWithPlaceType(final SeasonModel seasonModel,
			final CompetitionType competitionType,
			final Place place) {
		final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
		final List<Match> matchList = this.matchDAO.getMatchListWithPlaceType(season, competitionType, place);
		return matchList.stream().map(t -> MatchModel.toModel(t)).collect(Collectors.toList());
	}

	public List<MatchModel> getMatchListForCompetition(final SeasonModel seasonModel,
			final CompetitionType competitionType) {
		final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
		final List<Match> matchList = this.matchDAO.getMatchList(season, competitionType);
		return matchList.stream().map(t -> MatchModel.toModel(t)).collect(Collectors.toList());
	}

	public MatchModel addMatch(final MatchModel model) {
		final Match match = MatchModel.toEntity(model);
		final Match addedMatch = this.matchDAO.addMatch(match);
		return MatchModel.toModel(addedMatch);
	}
}
