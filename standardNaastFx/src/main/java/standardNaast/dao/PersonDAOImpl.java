package standardNaast.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import standardNaast.entities.Personne;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class PersonDAOImpl implements PersonDAO {

	@Override
	public Personne addPerson(final Personne person) {
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		entityManager.persist(person);
		entityManager.getTransaction().commit();
		entityManager.close();
		return person;
	}

	@Override
	public Personne updatePerson(final Personne personne) {
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		final Personne mergedPerson = entityManager.merge(personne);
		entityManager.getTransaction().commit();
		entityManager.close();
		return mergedPerson;
	}

	@Override
	public Personne getPerson(final long id) {
		final EntityManager entityManager = this.getEntityManager();
		final Personne find = entityManager.find(Personne.class, id);
		entityManager.refresh(find);
		return find;
	}

	@Override
	public Personne getPersonneByMemberNumber(final long memberNumber) {
		final TypedQuery<Personne> query = this.getEntityManager()
				.createNamedQuery("getByMemberNumber", Personne.class);
		query.setParameter("memberNumber", memberNumber);
		return query.getSingleResult();
	}

	@Override
	public List<Personne> getAllPersons(final boolean allPersons) {
		final CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		final CriteriaQuery<Personne> cq = cb.createQuery(Personne.class);
		final Root<Personne> personne = cq.from(Personne.class);
		cq.select(personne);
		if (!allPersons) {
			final ParameterExpression<Long> p = cb.parameter(Long.class, "memberNumber");
			final Path<Long> memberNumber = personne.get("memberNumber");
			cq.where(cb.lt(memberNumber, p));
		}
		final TypedQuery<Personne> query = this.getEntityManager().createQuery(cq);
		if (!allPersons) {
			query.setParameter("memberNumber", 10000);
		}
		return query.getResultList();
	}

	public List<Personne> getNonMembers() {
		// final CriteriaBuilder cb =
		// this.getEntityManager().getCriteriaBuilder();
		// final CriteriaQuery<Personne> cq = cb.createQuery(Personne.class);
		// final Root<Personne> personne = cq.from(Personne.class);
		// cq.select(personne);
		// if (!allPersons) {
		// final ParameterExpression<Long> p = cb.parameter(Long.class,
		// "memberNumber");
		// final Path<Long> memberNumber = personne.get("memberNumber");
		// cq.where(cb.lt(memberNumber, p));
		// }
		// final TypedQuery<Personne> query =
		// this.getEntityManager().createQuery(cq);
		// if (!allPersons) {
		// query.setParameter("memberNumber", 10000);
		// }
		// return query.getResultList();
		return new ArrayList<>();
	}

	public EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}

	@Override
	public Long getMaxMemberNumber() {
		final TypedQuery<Long> query = this.getEntityManager()
				.createNamedQuery("getMaxMemberNumber", Long.class);
		return query.getSingleResult();

	}

}
