/**
 *
 */
package standardNaast.service;

import java.util.List;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import standardNaast.dao.PersonDAO;
import standardNaast.entities.Personne;

/**
 * @author stessy
 * 
 */
@Named
@Service("personneService")
@Transactional(readOnly = true)
public class PersonneService {

	@Autowired
	PersonDAO personneDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneService.class);

	public PersonneService() {
		PersonneService.LOGGER.debug("Instanciating PersonneService");
	}

	public List<Personne> findAllPerson() {
		PersonneService.LOGGER.debug("Getting list of personnes");
		return this.personneDAO.getAllPersons();
	}

	public Personne getPerson(final long id) {
		return this.personneDAO.getPerson(id);
	}

	@Transactional(readOnly = false)
	public Personne savePerson(final Personne person) {
		return this.personneDAO.updatePerson(person);
	}

	@Transactional(readOnly = false)
	public Personne addPerson(final Personne person) {
		this.personneDAO.addPerson(person);
		return person;
	}

}
