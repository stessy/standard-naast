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

import standardNaast.entities.Personne;

/**
 * @author stessy
 * 
 */
@Stateless(mappedName = "PersonneService")
@LocalBean
public class PersonneService {

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneService.class);

	public List<Personne> findAllPerson() {
		PersonneService.LOGGER.debug("Getting list of personnes");
		CriteriaQuery<Personne> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Personne.class);
		queryAll.from(Personne.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

	public Personne getPerson(final long id) {
		return this.entityManager.find(Personne.class, id);
	}

}
