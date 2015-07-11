package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Match;
import standardNaast.entities.PersonneTravel;
import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class TravelDAOImpl implements TravelDAO {

	@Override
	public List<TravelPrice> getTravelsPerSeason(final Season season) {
		final TypedQuery<TravelPrice> query = this.getEntityManager().createNamedQuery("findBySeason",
				TravelPrice.class);
		query.setParameter("season", season);
		return query.getResultList();
	}

	@Override
	public List<PersonneTravel> getMatchTravels(final Match match) {
		final TypedQuery<PersonneTravel> query = this.getEntityManager().createNamedQuery("getMatchTravels",
				PersonneTravel.class);
		query.setParameter("match", match);
		return query.getResultList();
	}

	@Override
	public PersonneTravel addMemberMatchTravel(final PersonneTravel travel) {
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(travel.getMatch());
		entityManager.merge(travel.getMatch().getSeason());
		entityManager.merge(travel.getPersonne());
		entityManager.persist(travel);
		entityManager.getTransaction().commit();
		entityManager.close();
		return travel;
	}

	private EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}

	@Override
	public void removeMemberMatchTravel(final PersonneTravel travel) {
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.merge(travel.getMatch());
		entityManager.merge(travel.getMatch().getSeason());
		entityManager.merge(travel.getPersonne());
		final PersonneTravel mergedTravel = entityManager.find(PersonneTravel.class, travel.getId());
		entityManager.refresh(mergedTravel);
		entityManager.remove(mergedTravel);
		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
