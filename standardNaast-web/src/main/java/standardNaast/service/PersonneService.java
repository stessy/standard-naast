/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.dao.PersonDAO;
import standardNaast.entities.Personne;

/**
 * @author stessy
 * 
 */
@Stateless
@LocalBean
public class PersonneService implements Serializable {

	@Inject
	PersonDAO personneDAO;

	private static final Logger LOGGER = Logger.getLogger(PersonneService.class);

	public PersonneService() {
		PersonneService.LOGGER.debug("Instanciating PersonneService");
	}

	public List<Personne> findAllPerson(final boolean allPersons) {
		PersonneService.LOGGER.debug("Getting list of personnes");
		return this.personneDAO.getAllPersons(allPersons);
	}

	public Personne getPerson(final long id) {
		return this.personneDAO.getPerson(id);
	}

	public Personne getPersonByMemberNumber(final long memberNumber) {
		return this.personneDAO.getPersonneByMemberNumber(memberNumber);
	}

	public Personne savePerson(final Personne person) {
		return this.personneDAO.updatePerson(person);
	}

	public Personne addPerson(final Personne person) {
		this.personneDAO.addPerson(person);
		return person;
	}

}
