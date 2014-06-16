/**
 *
 */
package standardNaast.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Parameter;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import standardNaast.exception.TechnicalException;

/**
 * @author stessy
 * 
 */

public class GenericDAOImpl {

	/**
	 * The LOG.
	 */
	private static final Logger LOG = Logger.getLogger(GenericDAOImpl.class);

	/**
	 * Regex to validate a query parameter against positional values.
	 */
	private static final Pattern VALID_PARAM = Pattern.compile("^\\d*$");

	public <T> T persist(final T entity) {
		System.out.println("Persisting the entity [" + entity.toString() + "]");
		GenericDAOImpl.LOG.debug("Persisting the entity [" + entity.toString()
				+ "]");
		this.getEntityManager().persist(entity);
		return entity;
	}

	public <T> void detach(final T entity) {
		GenericDAOImpl.LOG.debug("Detaching the entity [" + entity.toString()
				+ "]");
		this.getEntityManager().detach(entity);
	}

	public <T> void detach(final List<T> entities) {
		for (T entity : entities) {
			this.getEntityManager().detach(entity);
		}
	}

	public <T> T merge(final T entity) {
		System.out.println("Merging the entity [" + entity.toString() + "]");
		GenericDAOImpl.LOG.debug("Merging the entity [" + entity.toString()
				+ "]");
		return this.getEntityManager().merge(entity);
	}

	public void flush() {
		GenericDAOImpl.LOG.debug("Merging the persistence context");
		this.getEntityManager().flush();
	}

	public boolean contains(final Object entity) {
		GenericDAOImpl.LOG.debug("Checking if the entity manager the entity ["
				+ entity.toString() + "]");
		return this.getEntityManager().contains(entity);
	}

	public <T> T findByPrimaryKey(final Class<T> entityType,
			final Object primaryKey) {
		GenericDAOImpl.LOG.debug("Find entity off type ["
				+ entityType.toString() + "] with primary key value ["
				+ primaryKey.toString() + "]");
		return this.getEntityManager().find(entityType, primaryKey);
	}

	public <T> List<T> findAll(final Class<T> entityType) {
		GenericDAOImpl.LOG.debug("Find all entities off type ["
				+ entityType.toString() + "]");
		CriteriaQuery<T> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(entityType);
		queryAll.from(entityType);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	public void remove(final Object entity) {
		GenericDAOImpl.LOG.debug("Removing the entity [" + entity.toString()
				+ "]");
		Object mergedEntity = this.getEntityManager().merge(entity); // merge it
																		// just
																		// to be
																		// sure
																		// its
																		// attached
		this.getEntityManager().remove(mergedEntity);
	}

	@SuppressWarnings("unchecked")
	public <T> T refresh(final T entity) {
		GenericDAOImpl.LOG.debug("Refreshing the entity [" + entity.toString()
				+ "]");
		Object mergedEntity = this.getEntityManager().merge(entity); // merge it
																		// just
																		// to be
																		// sure
																		// its
																		// attached
		this.getEntityManager().refresh(mergedEntity);
		return (T) mergedEntity;
	}

	public <T> List<T> refresh(final List<T> entities) {
		List<T> refreshedEntities = new ArrayList<T>();
		for (T entity : entities) {
			refreshedEntities.add(this.refresh(entity));
		}
		return refreshedEntities;
	}

	public <T> T getReference(final Class<T> entityClass,
			final Object primaryKey) {
		GenericDAOImpl.LOG.debug("Getting reference for entity [" + primaryKey
				+ "]");
		return this.getEntityManager().getReference(entityClass, primaryKey);
	}

	public int executeNamedQuery(final String name, final Object... parameters) {
		GenericDAOImpl.LOG.debug("Executing a named query [" + name + "]");
		Query query = this.getEntityManager().createNamedQuery(name);
		this.setQueryParameters(query, parameters);
		return query.executeUpdate();
	}

	public <T> List<T> getNamedQueryResultList(final String name,
			final Class<T> resultClass, final Object... parameters) {
		GenericDAOImpl.LOG.debug("Create a named query [" + name + "]");
		TypedQuery<T> typedQuery = this.getEntityManager().createNamedQuery(
				name, resultClass);
		this.setQueryParameters(typedQuery, parameters);
		return typedQuery.getResultList();
	}

	public <T> T getNamedQueryFirstResult(final String name,
			final Class<T> resultClass, final Object... parameters) {
		return this.getNamedQuerySingleResult(name, resultClass, false,
				parameters);
	}

	public <T> T getNamedQueryResult(final String name,
			final Class<T> resultClass, final Object... parameters) {
		return this.getNamedQuerySingleResult(name, resultClass, true,
				parameters);
	}

	/**
	 * Execute a named query passing along a set of optional parameters. The set
	 * of parameters must match the set defined in the query. After a
	 * {@link java.util.Date} or {@link java.util.Calendar} parameter, a
	 * {@link javax.persistence.TemporalType} parameters should be added.
	 * 
	 * @param <T>
	 *            the type of the entity
	 * @param name
	 *            the name of the query
	 * @param resultClass
	 *            the type of the query result
	 * @param parameters
	 *            the parameters to enter into the query
	 * @param failOnMultipleResults
	 *            if multiple results are provided, fail (true) or just return
	 *            the first one (false).
	 * @return the result of the query or null if no result was found
	 */
	private <T> T getNamedQuerySingleResult(final String name,
			final Class<T> resultClass, final boolean failOnMultipleResults,
			final Object... parameters) {
		GenericDAOImpl.LOG.debug("Create a named query [" + name + "]");
		TypedQuery<T> typedQuery = this.getEntityManager().createNamedQuery(
				name, resultClass);
		if (!failOnMultipleResults) {
			typedQuery.setMaxResults(1);
		}
		this.setQueryParameters(typedQuery, parameters);
		List<T> results = typedQuery.getResultList();
		return this.getSingleResult(results);
	}

	public <T> List<T> getCriteriaBuilderResultList(
			final CriteriaQueryBuilder<T> cqb) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cQuery = cqb.createCriteriaQuery(cb);
		TypedQuery<T> tQuery = this.getEntityManager().createQuery(cQuery);
		return tQuery.getResultList();
	}

	public <T> T getCriteriaBuilderResult(final CriteriaQueryBuilder<T> cqb) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cQuery = cqb.createCriteriaQuery(cb);
		TypedQuery<T> tQuery = this.getEntityManager().createQuery(cQuery);
		return this.getSingleResult(tQuery.getResultList());
	}

	public void clearCache() {
		this.getEntityManager().getEntityManagerFactory().getCache().evictAll();
	}

	/**
	 * Utility method to get single result from result list.
	 * 
	 * @param <T>
	 *            the result type
	 * @param results
	 *            the results to get the single result from
	 * @return null or a single result
	 */
	private <T> T getSingleResult(final List<T> results) {
		if ((results != null) && (results.size() == 1)) {
			return results.get(0);
		} else if ((results != null) && (results.size() > 1)) {
			throw new TechnicalException("Several results found, expected one");
		} else {
			return null;
		}
	}

	/**
	 * Pass an optional set of parameters to a query. The set of parameters must
	 * match the set defined in the query. After a {@link java.util.Date} or
	 * {@link java.util.Calendar} parameter, a
	 * {@link javax.persistence.TemporalType} parameters should be added.
	 * 
	 * @param query
	 *            the query to execute
	 * @param parameters
	 *            the parameters to enter into the query
	 */
	private void setQueryParameters(final Query query,
			final Object... parameters) {
		this.validateQuery(query);
		this.validateInputParameters(parameters);

		// set query
		for (int position = 0; position < parameters.length; position++) {
			Object parameter = parameters[position];

			if (parameter.getClass().equals(Calendar.class)) {
				query.setParameter(position + 1, (Calendar) parameter,
						(TemporalType) parameters[position + 1]);
				position++;
			} else if (parameter.getClass().equals(Date.class)) {
				query.setParameter(position + 1, (Date) parameter,
						(TemporalType) parameters[position + 1]);
				position++;
			} else {
				query.setParameter(position + 1, parameter);
			}
		}
	}

	/**
	 * Utility method to verify if a query is properly created. This means only
	 * positional parameters, no named parameters. The first positional
	 * parameter should be 1, not 0.
	 * 
	 * @param query
	 *            the query to validate
	 */
	private void validateQuery(final Query query) {
		Set<Parameter<?>> parameters = query.getParameters();
		int sum = 0;
		int max = 0;

		for (Parameter<?> param : parameters) {
			Matcher paramMatcher = GenericDAOImpl.VALID_PARAM.matcher(param
					.getName());

			if (!paramMatcher.matches()) {
				throw new IllegalArgumentException(
						"Only positional parameters are supported, no named parameters.");
			} else if (paramMatcher.matches()
					&& (Integer.parseInt(param.getName()) == 0)) {
				throw new IllegalArgumentException(
						"Positional parameters start at 1.");
			} else {
				sum += Integer.parseInt(param.getName());
				max = Math.max(max, Integer.parseInt(param.getName()));
			}
		}

		if (((max * (max + 1)) / 2) != sum) {
			throw new IllegalArgumentException(
					"Positional parameters should start at 1, and continue to be incremented by one for each new paramter.");
		}
	}

	/**
	 * Validate a set of parameters to check that the temporal type enum is
	 * present right after the temporal type.
	 * 
	 * @param parameters
	 *            the paramaters to validate
	 */
	private void validateInputParameters(final Object[] parameters) {
		boolean temporal = false;
		for (Object parameter : parameters) {
			if (temporal) {
				if (!parameter.getClass().equals(TemporalType.class)) {
					throw new IllegalArgumentException(
							"After a Date or Calendar, a TemporalType is expected.");
				} else {
					temporal = false;
				}
			} else if (parameter.getClass().equals(Calendar.class)
					|| parameter.getClass().equals(Date.class)) {
				temporal = true;
			}
		}
	}

	/**
	 * @return Returns the entityManager.
	 */
	public EntityManager getEntityManager() {
		EntityManagerFactory entityManagerFactory = Persistence
				.createEntityManagerFactory("StandardNaastPeristenceUnit");
		EntityManager entityManager = entityManagerFactory
				.createEntityManager();
		return entityManager;
	}
}
