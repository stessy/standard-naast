/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import standardNaast.entities.Cotisation;

/**
 * @author stessy
 * 
 */
@Stateless
@LocalBean
public class CotisationsService implements Serializable {

	private static final long serialVersionUID = -3608269572256839874L;

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger LOGGER = Logger
			.getLogger(CotisationsService.class);

	public List<Cotisation> findAllCotisations() {
		CotisationsService.LOGGER.debug("Getting list of cotisations");
		CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

}
