/**
 *
 */
package standardNaast.service;

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
@Stateless(mappedName = "CotisationService")
@LocalBean
public class CotisationService {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger LOGGER = Logger
			.getLogger(CotisationService.class);

	public List<Cotisation> findAllCotisations() {
		CotisationService.LOGGER.debug("Getting list of cotisations");
		CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

}
