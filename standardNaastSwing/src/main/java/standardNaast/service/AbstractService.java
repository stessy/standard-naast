/**
 * 
 */
package standardNaast.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import standardNaast.dao.AbstractGenericDAO;
import standardNaast.dao.GenericDAOImpl;

/**
 * @author stessy
 * 
 */
public abstract class AbstractService implements Service {

	protected final AbstractGenericDAO abstractGenericDAO;

	protected final EntityManager em;

	protected final EntityTransaction transaction;


	public AbstractService() {
		this.abstractGenericDAO = new GenericDAOImpl();
		this.em = this.abstractGenericDAO.getEntityManager();
		this.transaction = this.em.getTransaction();
	}


	@Override
	public <T> void add(final T entity) {

		this.transaction.begin();
		this.em.persist(entity);
		this.transaction.commit();

	}


	@Override
	public <T> T update(final T entity) {

		this.transaction.begin();
		T managedEntity = this.em.merge(entity);
		this.transaction.commit();
		return managedEntity;

	}


	@Override
	public <T> void remove(final T entity) {
		this.transaction.begin();
		this.abstractGenericDAO.remove(entity);
		this.transaction.commit();
	}

}
