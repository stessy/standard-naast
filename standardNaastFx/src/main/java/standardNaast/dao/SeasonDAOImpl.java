package standardNaast.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.collections4.CollectionUtils;

import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.types.CompetitionType;
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
		final TypedQuery<Long> query = this.entityManager.createNamedQuery("getMemberTravelsPerSeason",
				Long.class);
		query.setParameter("season", season);
		query.setParameter("person", member);
		query.setParameter("competitionsType", Arrays.asList(CompetitionType.CHAMPIONSHIP, CompetitionType.PLAYOFFS));
		query.setParameter("place", away ? Place.AWAY : Place.HOME);
		return query.getSingleResult().intValue();
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

	@Override
	public Season getSeasonForSpecificDate(final Date date) {
		final TypedQuery<Season> query = this.entityManager.createNamedQuery("getSeasonForDate",
				Season.class);
		query.setParameter("date", date);
		final List<Season> resultList = query.getResultList();
		if (CollectionUtils.isEmpty(resultList)) {
			return null;
		} else {
			return resultList.get(0);
		}
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
