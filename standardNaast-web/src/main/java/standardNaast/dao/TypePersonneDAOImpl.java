package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import standardNaast.entities.TypePersonne;

public class TypePersonneDAOImpl implements TypePersonneDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<TypePersonne> getAllTypePersonne() {
		CriteriaQuery<TypePersonne> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(TypePersonne.class);
		queryAll.from(TypePersonne.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
