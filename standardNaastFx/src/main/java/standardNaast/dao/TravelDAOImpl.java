package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class TravelDAOImpl implements TravelDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public List<TravelPrice> getTravelsPerSeason(final Season season) {
		final TypedQuery<TravelPrice> query = this.entityManager.createNamedQuery("findBySeason", TravelPrice.class);
		query.setParameter("season", season);
		return query.getResultList();
	}
}
