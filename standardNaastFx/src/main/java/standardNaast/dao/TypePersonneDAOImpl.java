package standardNaast.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class TypePersonneDAOImpl implements TypePersonneDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
