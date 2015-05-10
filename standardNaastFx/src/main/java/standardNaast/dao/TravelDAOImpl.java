package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Season;
import standardNaast.entities.Travel;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class TravelDAOImpl implements TravelDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public List<Travel> getAllTravels() {
		this.entityManager.getTransaction().begin();
		final CriteriaQuery<Travel> queryAll = this.entityManager.getCriteriaBuilder().createQuery(Travel.class);
		queryAll.from(Travel.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

	public List<Travel> getTravelsPerSeason(final Season season) {
		this.entityManager.getTransaction().begin();
		final CriteriaQuery<Travel> queryAll = this.entityManager.getCriteriaBuilder().createQuery(Travel.class);
		queryAll.from(Travel.class);
		queryAll.where(arg0)
		return this.entityManager.createQuery(queryAll).getResultList();
	}
}
