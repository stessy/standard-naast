package standardNaast.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import standardNaast.entities.Match;
import standardNaast.entities.Match_;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonnesMatch;
import standardNaast.entities.PersonnesMatch_;
import standardNaast.entities.Saison;
import standardNaast.entities.SaisonEquipe;
import standardNaast.entities.SaisonEquipe_;

public class SaisonDAOImpl implements SaisonDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Saison> getAllSeasons() {
		CriteriaQuery<Saison> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Saison.class);
		queryAll.from(Saison.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	@Override
	public Saison getSeasonById(String season) {
		return this.getEntityManager().find(Saison.class, season);
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public int getTravelsPerSeasonAway(Saison season, Personne member) {
		return this.getTravelsPerSeason(season, member, true);
	}

	@Override
	public int getTravelsPerSeasonHome(Saison season, Personne member) {
		return this.getTravelsPerSeason(season, member, false);
	}

	private int getTravelsPerSeason(Saison season, Personne member, boolean away) {
		String lieu = away ? "DEPLACEMENT" : "DOMICILE";
		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PersonnesMatch> root = cq.from(PersonnesMatch.class);
		Join<PersonnesMatch, Match> match = root.join(PersonnesMatch_.match);
		Join<Match, SaisonEquipe> saisonEquipe = match
				.join(Match_.saisonEquipe);
		cq.select(cb.count(root));
		List<Predicate> criteria = new ArrayList<Predicate>();
		criteria.add(cb.equal(cb.upper(match.get(Match_.lieu)), lieu));
		criteria.add(cb.equal(root.get(PersonnesMatch_.personne), member));
		criteria.add(cb.equal(saisonEquipe.get(SaisonEquipe_.saison), season));
		cq.where(cb.and(criteria.toArray(new Predicate[0])));
		TypedQuery<Long> createQuery = this.entityManager.createQuery(cq);
		return createQuery.getSingleResult().intValue();
	}

	// public static void main(String args[]) {
	// EntityManagerFactory emf = Persistence
	// .createEntityManagerFactory("StandardNaastPeristenceUnit");
	// SaisonDAOImpl.entityManager = emf.createEntityManager();
	// SaisonDAOImpl.entityManager.getTransaction().begin();
	// SaisonDAOImpl saisonDAOImpl = new SaisonDAOImpl();
	// Personne p = new Personne();
	// p.setPersonneId(252);
	// Saison s = new Saison();
	// s.setDateStart(DateTime.now().minusYears(4).toDate());
	// s.setDateEnd(DateTime.now().minusYears(3).toDate());
	// s.setId("2008-2009");
	// System.out.println(saisonDAOImpl.getTravelsPerSeason(s, p, false));
	// System.out.println(saisonDAOImpl.getTravelsPerSeason(s, p, true));
	// }
}
