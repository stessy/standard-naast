package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Match;
import standardNaast.entities.Season;
import standardNaast.entities.Team;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class MatchDAOImpl implements MatchDAO {

	@Override
	public Match addMatch(final Match match) {
		final EntityManager entityManager = EntityManagerFactoryHelper
				.getFactory().createEntityManager();
		entityManager.getTransaction().begin();
		final Team mergedOpponent = entityManager.find(Team.class, match.getOpponent().getEquipeId());
		final Season mergedSeason = entityManager.find(Season.class, match.getSeason().toString());
		match.setOpponent(mergedOpponent);
		match.setSeason(mergedSeason);
		final Match addedMatch = entityManager.merge(match);
		entityManager.getTransaction().commit();
		return addedMatch;
	}

	@Override
	public List<Match> getMatchListWithMatchType(final Season season, final CompetitionType competitiontype,
			final MatchType matchType) {
		final TypedQuery<Match> namedQuery = this.getEntityManager().createNamedQuery("matchListWithMatchType",
				Match.class);
		namedQuery.setParameter("season", season);
		namedQuery.setParameter("competitionType", competitiontype);
		namedQuery.setParameter("matchType", matchType);
		return namedQuery.getResultList();
	}

	@Override
	public List<Match> getMatchListWithPlaceType(final Season season, final CompetitionType competitiontype,
			final Place place) {
		final TypedQuery<Match> namedQuery = this.getEntityManager()
				.createNamedQuery("matchListWithPlace", Match.class);
		namedQuery.setParameter("season", season);
		namedQuery.setParameter("competitionType", competitiontype);
		namedQuery.setParameter("place", place);
		return namedQuery.getResultList();
	}

	@Override
	public List<Match> getMatchList(final Season season, final CompetitionType competitiontype) {
		final TypedQuery<Match> namedQuery = this.getEntityManager().createNamedQuery("matchList",
				Match.class);
		namedQuery.setParameter("season", season);
		namedQuery.setParameter("competitionType", competitiontype);
		return namedQuery.getResultList();
	}

	@Override
	public Match getMatch(final long id) {
		return this.getEntityManager().find(Match.class, id);
	}

	private EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}
}
