package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Team;

public class TeamDAOImpl implements TeamDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Team> getAllTeams() {
		CriteriaQuery<Team> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Team.class);
		queryAll.from(Team.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
