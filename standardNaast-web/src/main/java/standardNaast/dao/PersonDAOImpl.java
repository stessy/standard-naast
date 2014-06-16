package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

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
	public List<Personne> getAllPersons() {
		CriteriaQuery<Personne> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Personne.class);
		queryAll.from(Personne.class);
		List<Personne> allPersons = this.getEntityManager()
				.createQuery(queryAll).getResultList();
		return allPersons;

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
		// return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
