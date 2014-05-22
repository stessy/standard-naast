/* Copyright 2011 BuyWay-Services */
package standardNaast.dao;

import java.util.List;

/**
 * Description : a generic DAO.
 *
 * @author BuyWay-Services: DWW<BR>
 * @version $Revision: $Date: $<BR> Created on 31 janv. 2012
 */
public interface GenericDAO {

    /**
     * Make an entity instance managed and persistent.
     *
     * @param <T> the type of the entity
     * @param entity the entity to persist
     * @return the persisted entity
     */
    <T> T persist(T entity);

    /**
     * Merge the state of the given entity into the current persistence context.
     *
     * @param <T> the type of the entity
     * @param entity entity instance
     * @return the managed instance that the state was merged to
     */
    <T> T merge(T entity);

    /**
     * Check if the instance is a managed entity instance belonging to the
     * current persistence context.
     *
     * @param entity the entity to check for
     * @return true if the entity is managed, false otherwise
     */
    boolean contains(Object entity);

    /**
     * Find by primary key.
     *
     * @param <T> the type parameter
     * @param entityType the type of the entity to find
     * @param primaryKey the primary key of the entity
     * @return the found entity instance or null if the entity does not exist
     */
    <T> T findByPrimaryKey(Class<T> entityType, Object primaryKey);

    /**
     * @param <T> the type of the result
     * @param entityType the type of the entity to find
     * @return All occurrences of the entity type in the db
     */
    <T> List<T> findAll(Class<T> entityType);

    /**
     * Remove the entity instance.
     *
     * @param entity the entity to remove
     */
    void remove(Object entity);

    /**
     * Synchronize the persistence context to the underlying database.
     */
    void flush();

    /**
     * Refresh the state of the instance from the database, overwriting changes
     * made to the entity, if any.
     *
     * @param <T> the type of the parameter
     * @param entity the entity to refresh
     * @return the refreshed entity
     */
    <T> T refresh(T entity);

    /**
     * Get an instance, whose state may be lazily fetched. If the requested
     * instance does not exist in the database, the EntityNotFoundException is
     * thrown when the instance state is first accessed. (The persistence
     * provider runtime is permitted to throw the EntityNotFoundException when
     * getReference is called.) The application should not expect that the
     * instance state will be available upon detachment, unless it was accessed
     * by the application while the entity manager was open.
     *
     * @param <T> the entity type
     * @param entityClass entity class
     * @param primaryKey primary key
     * @return the found entity instance
     */
    <T> T getReference(Class<T> entityClass, Object primaryKey);

    /**
     * Detach an entity from the persistence context.
     *
     * @param <T> the type of the entity
     * @param entity the entity to detach
     */
    <T> void detach(T entity);

    /**
     * Detach entities from the persistence context.
     *
     * @param <T> the type of the entity
     * @param entities the entities to detach
     */
    <T> void detach(List<T> entities);

    /**
     * Refresh the state of each instance from the database, overwriting changes
     * made to the entities, if any.
     *
     * @param <T> the type of the parameter
     * @param entities the entities to refresh
     * @return the refreshed entities
     */
    <T> List<T> refresh(List<T> entities);

    /**
     * Execute a named query without expecting any result.
     *
     * @param name the name of the named query to execute
     * @param parameters the parameters to enter into the query
     * @return number of results.
     */
    int executeNamedQuery(final String name, Object... parameters);


    /*
     * Removed support for dynamic queries, as named queries are the preferred choice. Commenting in the interface
     * allows us to re-enable this functionality easily.
     */
    // /**
    // * Execute a query passing along a set of optional parameters. The set of parameters must match the set defined in
    // * the query. After a {@link java.util.Date} or {@link java.util.Calendar} parameter, a
    // * {@link javax.persistence.TemporalType} parameters should be added.
    // *
    // * @param qlString the query
    // * @param parameters the parameters to enter into the query
    // * @return the result of the query
    // */
    // public List<?> getQueryResultList(String qlString, Object... parameters);
    /**
     * Execute a named query passing along a set of optional parameters. The set
     * of parameters must match the set defined in the query. After a
     * {@link java.util.Date} or {@link java.util.Calendar} parameter, a
     * {@link javax.persistence.TemporalType} parameters should be added.
     *
     * @param <T> the type of the entity
     * @param name the name of the query
     * @param resultClass the type of the query result
     * @param parameters the parameters to enter into the query
     * @return the result of the query
     */
    <T> List<T> getNamedQueryResultList(String name, Class<T> resultClass, Object... parameters);


    /*
     * Removed support for dynamic queries, as named queries are the preferred choice. Commenting in the interface
     * allows us to re-enable this functionality easily.
     */
    // /**
    // * Execute a query passing along a set of optional parameters. The set of parameters must match the set defined in
    // * the query. After a {@link java.util.Date} or {@link java.util.Calendar} parameter, a
    // * {@link javax.persistence.TemporalType} parameters should be added.
    // *
    // * @param qlString the query
    // * @param parameters the parameters to enter into the query
    // * @return the result of the query or null if no result was found
    // */
    // public Object getQueryResult(String qlString, Object... parameters) throws BusinessException;
    /**
     * Execute a named query passing along a set of optional parameters, and
     * retrieving the first row only. The set of parameters must match the set
     * defined in the query. After a {@link java.util.Date} or
     * {@link java.util.Calendar} parameter, a
     * {@link javax.persistence.TemporalType} parameters should be added.
     *
     * @param <T> the type of the entity
     * @param name the name of the query
     * @param resultClass the type of the query result
     * @param parameters the parameters to enter into the query
     * @return the result of the query, or null if no result
     */
    <T> T getNamedQueryFirstResult(final String name, final Class<T> resultClass, final Object... parameters);

    /**
     * Execute a named query passing along a set of optional parameters. If
     * multiple results are returned, throw an exception. The set of parameters
     * must match the set defined in the query. After a {@link java.util.Date}
     * or {@link java.util.Calendar} parameter, a
     * {@link javax.persistence.TemporalType} parameters should be added.
     *
     * @param <T> the type of the entity
     * @param name the name of the query
     * @param resultClass the type of the query result
     * @param parameters the parameters to enter into the query
     * @return the result of the query or null if no result was found
     */
    <T> T getNamedQueryResult(String name, Class<T> resultClass, Object... parameters);

    /**
     * Execute a query build by a criteria builder. This method requires a
     * {@link CriteriaQueryBuilder} implementation to be passed.
     *
     * @param <T> the type of the result
     * @param cqb the CriteriaQueryBuilder to use
     * @return a list with the results
     */
    <T> List<T> getCriteriaBuilderResultList(CriteriaQueryBuilder<T> cqb);

    /**
     * Execute a query build by a criteria builder. This method requires a
     * {@link CriteriaQueryBuilder} implementation to be passed.
     *
     * @param <T> the type of the result
     * @param cqb the CriteriaQueryBuilder to use
     * @return the result or null
     */
    <T> T getCriteriaBuilderResult(CriteriaQueryBuilder<T> cqb);

    /**
     * Clear the business cache.
     */
    void clearCache();
}
