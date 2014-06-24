package standardNaast.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

import standardNaast.entities.Match;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonnesMatch;
import standardNaast.entities.Season;
import types.Place;

public class SaisonDAOImpl implements SaisonDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Season> getAllSeasons() {
		CriteriaQuery<Season> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Season.class);
		queryAll.from(Season.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	@Override
	public Season getSeasonById(String season) {
		return this.getEntityManager().find(Season.class, season);
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public int getTravelsPerSeasonAway(Season season, Personne member) {
		return this.getTravelsPerSeason(season, member, true);
	}

	@Override
	public int getTravelsPerSeasonHome(Season season, Personne member) {
		return this.getTravelsPerSeason(season, member, false);
	}

	private int getTravelsPerSeason(Season season, Personne member, boolean away) {
		Place place = away ? Place.AWAY : Place.HOME;
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PersonnesMatch> root = cq.from(PersonnesMatch.class);
		Join<PersonnesMatch, Match> match = root.join("match");
		cq.select(cb.count(root));
		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(match.get("place"), place));
		criteria.add(cb.equal(root.get("personne"), member));
		criteria.add(cb.equal(match.get("season"), season));
		cq.where(cb.and(criteria.toArray(new Predicate[0])));
		TypedQuery<Long> createQuery = this.entityManager.createQuery(cq);
		return createQuery.getSingleResult().intValue();
	}

	public static void main(String args[]) {
		new SaisonDAOImpl();
	}

	public SaisonDAOImpl() {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("StandardNaastPeristenceUnit");
		this.entityManager = emf.createEntityManager();
		this.entityManager.getTransaction().begin();
		Personne p = new Personne();
		p.setPersonneId(252);
		Season s = new Season();
		s.setDateStart(DateTime.now().minusYears(4).toDate());
		s.setDateEnd(DateTime.now().minusYears(3).toDate());
		s.setId("2008-2009");
		System.out.println(this.getTravelsPerSeason(s, p, false));
		System.out.println(this.getTravelsPerSeason(s, p, true));

	}
}
