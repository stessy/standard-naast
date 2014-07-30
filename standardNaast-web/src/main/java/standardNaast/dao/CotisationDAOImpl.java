package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Cotisation;

public class CotisationDAOImpl implements CotisationDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Cotisation> getAllCotisations() {
		CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();

	}

	@Override
	public List<Cotisation> getAllCotisationsPerYear(final String year) {
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<Cotisation> cq = cb.createQuery(Cotisation.class);
		cq.from(Cotisation.class);
		TypedQuery<Cotisation> q = this.entityManager.createQuery(cq);
		return q.getResultList();

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
