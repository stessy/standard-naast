package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import standardNaast.entities.Cotisation;

@Repository
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
		CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
