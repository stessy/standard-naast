/**
 *
 */
package standardNaast.service;

import java.util.List;

import javax.ejb.Local;
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
@Local(PersonneService.class)
public class PersonneServiceImpl implements PersonneService {

	@Inject
	PersonDAO personneDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneServiceImpl.class);

	public PersonneServiceImpl() {
		PersonneServiceImpl.LOGGER.debug("Instanciating PersonneService");
	}

	@Override
	public List<Personne> findAllPerson(final boolean allPersons) {
		PersonneServiceImpl.LOGGER.debug("Getting list of personnes");
		return this.personneDAO.getAllPersons(allPersons);
	}

	@Override
	public Personne getPerson(final long id) {
		return this.personneDAO.getPerson(id);
	}

	@Override
	public Personne getPersonByMemberNumber(final long memberNumber) {
		return this.personneDAO.getPersonneByMemberNumber(memberNumber);
	}

	@Override
	public Personne savePerson(final Personne person) {
		return this.personneDAO.updatePerson(person);
	}

	@Override
	public Personne addPerson(final Personne person) {
		this.personneDAO.addPerson(person);
		return person;
	}

}
