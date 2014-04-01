/**
 *
 */
package standardNaast.service;

import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import standardNaast.entities.Cotisation;
import standardNaast.entities.PersonneCotisation;

/**
 * @author stessy
 * 
 */
@Named
@Service("cotisationService")
@Transactional(readOnly = true)
public class PersonneCotisationsService {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneCotisationsService.class);

	public List<PersonneCotisation> findAllCotisations() {
		PersonneCotisationsService.LOGGER.debug("Getting list of cotisations");
		CriteriaQuery<PersonneCotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(PersonneCotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

}
