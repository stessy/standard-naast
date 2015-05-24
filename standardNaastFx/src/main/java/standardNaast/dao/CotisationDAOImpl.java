package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.Cotisation;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.entities.Season;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class CotisationDAOImpl implements CotisationDAO {

	@Override
	public List<Cotisation> getAllCotisations() {
		final EntityManager entityManager = this.getEntityManager();
		final CriteriaQuery<Cotisation> queryAll = entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		final List<Cotisation> allCotisations = entityManager.createQuery(queryAll).getResultList();
		return allCotisations;

	}

	@Override
	public List<Cotisation> getAllCotisationsPerYear(final String year) {
		final EntityManager entityManager = this.getEntityManager();
		final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		final CriteriaQuery<Cotisation> cq = cb.createQuery(Cotisation.class);
		cq.from(Cotisation.class);
		final TypedQuery<Cotisation> q = entityManager.createQuery(cq);
		return q.getResultList();
	}

	@Override
	public List<PersonneCotisation> getMemberCotisation(final Personne member, final Season season) {
		final EntityManager entityManager = this.getEntityManager();
		final TypedQuery<PersonneCotisation> query = entityManager.createNamedQuery("gePersonneCotisationPerSeason",
				PersonneCotisation.class);
		query.setParameter("season", season);
		query.setParameter("personne", member);
		final List<PersonneCotisation> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public List<PersonneCotisation> getMemberCotisations(final Personne member) {
		final EntityManager entityManager = this.getEntityManager();
		final TypedQuery<PersonneCotisation> query = entityManager.createNamedQuery("gePersonneCotisations",
				PersonneCotisation.class);
		query.setParameter("personne", member);
		final List<PersonneCotisation> resultList = query.getResultList();
		return resultList;
	}

	@Override
	public PersonneCotisation addMemberCotisation(final PersonneCotisation cotisation) {
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(cotisation);
		entityManager.getTransaction().commit();
		entityManager.close();
		return cotisation;
	}

	public EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}

}
