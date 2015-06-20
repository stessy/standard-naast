package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Season;
import standardNaast.entities.Team;
import standardNaast.types.CompetitionType;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class TeamDAOImpl implements TeamDAO {

	private EntityManager entityManager = EntityManagerFactoryHelper
			.getFactory().createEntityManager();

	@Override
	public List<Team> getAllTeams() {
		final CriteriaQuery<Team> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Team.class);
		queryAll.from(Team.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();
	}

	@Override
	public List<Team> getTeamsPerSeason(final Season season) {
		final TypedQuery<Team> query = this.getEntityManager()
				.createNamedQuery("teamPerSeason", Team.class);
		query.setParameter("id", season);
		return query.getResultList();
	}

	public List<Team> getTeamsPerSeasonAndCompetitionType(final Season season,
			final CompetitionType competitionType) {
		final TypedQuery<Team> query = this.getEntityManager()
				.createNamedQuery("teamPerSeason", Team.class);
		query.setParameter("id", season);
		return query.getResultList();
	}

	@Override
	public Team addTeam(final Team team) {
		this.getEntityManager().getTransaction().begin();
		this.getEntityManager().persist(team);
		this.getEntityManager().getTransaction().commit();
		return team;
	}

	@Override
	public Team updateTeam(final Team team) {
		this.getEntityManager().getTransaction().begin();
		final Team mergedTeam = this.getEntityManager().merge(team);
		this.getEntityManager().getTransaction().commit();
		return mergedTeam;
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
