/* Copyright 2011 BuyWay-Services */
package standardNaast.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * Description : an interface to be used in combination with the generic DAO. It
 * allows you to build queries using the criteria builder.
 *
 * @param <T> the type of the result
 * @author BuyWay-Services: DWW<BR>
 * @version $Revision: $Date: $<BR> Created on 23 févr. 2012
 */
public interface CriteriaQueryBuilder<T> {

    /**
     * This method will be invoked by the generic DAO in order to retrieve a
     * criteria query.
     *
     * @param cb the criteria builder to use
     * @return the criteria query created
     */
    CriteriaQuery<T> createCriteriaQuery(CriteriaBuilder cb);
}
