package standardNaast.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import standardNaast.entities.Match;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneTravel;
import standardNaast.entities.Season;
import standardNaast.types.Place;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class SeasonDAOImpl implements SeasonDAO {

	private EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public List<Season> getAllSeasons() {
		final CriteriaQuery<Season> queryAll = this.getEntityManager().getCriteriaBuilder().createQuery(Season.class);
		queryAll.from(Season.class);
		final List<Season> seasons = this.getEntityManager().createQuery(queryAll).getResultList();
		for (final Season season : seasons) {
			this.entityManager.refresh(season);
		}
		Collections.sort(seasons);
		Collections.reverse(seasons);
		return seasons;

	}

	@Override
	public Season getSeasonById(final String season) {
		return this.getEntityManager().find(Season.class, season);
	}

	public EntityManager getEntityManager() {
		// return EntityManagerFactoryHelper.getFactory().createEntityManager();
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public int getTravelsPerSeasonAway(final Season season, final Personne member) {
		return this.getTravelsPerSeason(season, member, true);
	}

	@Override
	public int getTravelsPerSeasonHome(final Season season, final Personne member) {
		return this.getTravelsPerSeason(season, member, false);
	}

	private int getTravelsPerSeason(final Season season, final Personne member, final boolean away) {
		final Place place = away ? Place.AWAY : Place.HOME;
		final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();

		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<PersonneTravel> root = cq.from(PersonneTravel.class);
		final Join<PersonneTravel, Match> match = root.join("match");
		cq.select(cb.count(root));
		final List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(match.get("place"), place));
		criteria.add(cb.equal(root.get("personne"), member));
		criteria.add(cb.equal(match.get("season"), season));
		cq.where(cb.and(criteria.toArray(new Predicate[0])));
		final TypedQuery<Long> createQuery = this.getEntityManager().createQuery(cq);
		return createQuery.getSingleResult().intValue();
	}

	@Override
	public Season merge(final Season season) {
		this.entityManager.getTransaction().begin();
		final Season mergedSeason = this.entityManager.merge(season);
		this.entityManager.getTransaction().commit();
		return mergedSeason;
	}

	@Override
	public Season addSeason(final Season season) {
		this.entityManager.getTransaction().begin();
		season.setId(season.toString());
		this.entityManager.persist(season);
		this.entityManager.getTransaction().commit();
		return season;
	}

	public static void main(final String args[]) {
		new SeasonDAOImpl();
	}

	// public SaisonDAOImpl() {
	// EntityManagerFactory emf = Persistence
	// .createEntityManagerFactory("StandardNaastPeristenceUnit");
	// this.entityManager = emf.createEntityManager();
	// this.entityManager.getTransaction().begin();
	// Personne p = new Personne();
	// p.setPersonneId(252);
	// Season s = new Season();
	// s.setDateStart(DateTime.now().minusYears(4).toDate());
	// s.setDateEnd(DateTime.now().minusYears(3).toDate());
	// s.setId("2008-2009");
	// System.out.println(this.getTravelsPerSeason(s, p, false));
	// System.out.println(this.getTravelsPerSeason(s, p, true));
	//
	// }
}
