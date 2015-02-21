package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Travel;

public class TravelDAOImpl implements TravelDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	@Override
	public List<Travel> getAllTravels() {
		CriteriaQuery<Travel> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Travel.class);
		queryAll.from(Travel.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();
	}

}
