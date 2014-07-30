package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import standardNaast.entities.Personne;

public class PersonDAOImpl implements PersonDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void addPerson(final Personne person) {
		this.getEntityManager().persist(person);
	}

	@Override
	public Personne updatePerson(final Personne personne) {
		return this.getEntityManager().merge(personne);
	}

	@Override
	public Personne getPerson(final long id) {
		return this.getEntityManager().find(Personne.class, id);
	}

	@Override
	public Personne getPersonneByMemberNumber(long memberNumber) {
		TypedQuery<Personne> query = this.getEntityManager().createNamedQuery("getByMemberNumber", Personne.class);
		query.setParameter("memberNumber", memberNumber);
		// query.setParameter(1, memberNumber);
		return query.getSingleResult();
	}

	@Override
	public List<Personne> getAllPersons(final boolean allPersons) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Personne> cq = cb.createQuery(Personne.class);
		Root<Personne> personne = cq.from(Personne.class);
		cq.select(personne);
		if (!allPersons) {
			ParameterExpression<Long> p = cb.parameter(Long.class, "memberNumber");
			Path<Long> memberNumber = personne.get("memberNumber");
			cq.where(cb.lt(memberNumber, p));
		}
		TypedQuery<Personne> query = this.getEntityManager().createQuery(cq);
		if (!allPersons) {
			query.setParameter("memberNumber", 10000);
		}
		return query.getResultList();
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
