/**
 *
 */
package standardNaast.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.entities.Personne;
import standardNaast.model.PersonModel;

public class PersonneServiceImpl implements PersonneService {

	PersonDAO personneDAO = new PersonDAOImpl();

	private static final Logger LOGGER = Logger
			.getLogger(PersonneServiceImpl.class);

	public PersonneServiceImpl() {
		PersonneServiceImpl.LOGGER.debug("Instanciating PersonneService");
	}

	@Override
	public List<PersonModel> findAllPerson(final boolean includeNonMembers) {
		PersonneServiceImpl.LOGGER.debug("Getting list of personnes");
		final List<Personne> allPersons = this.personneDAO.getAllPersons(includeNonMembers);
		final List<PersonModel> personsModel = allPersons.stream().map(t -> PersonModel.toModel(t))
				.collect(Collectors.toList());
		return personsModel;
	}

	@Override
	public PersonModel getPerson(final long id) {
		return PersonModel.toModel(this.personneDAO.getPerson(id));
	}

	@Override
	public PersonModel getPersonByMemberNumber(final long memberNumber) {
		return PersonModel.toModel(this.personneDAO.getPersonneByMemberNumber(memberNumber));
	}

	@Override
	public PersonModel savePerson(final PersonModel model) {
		final Personne person = this.personneDAO.getPerson(model.getPersonneId());
		final Personne entity = PersonModel.toUpdateEntity(model, person);
		return PersonModel.toModel(this.personneDAO.updatePerson(entity));
	}

	@Override
	public PersonModel addPerson(final PersonModel model) {
		final Personne entity = PersonModel.toEntity(model);
		return PersonModel.toModel(this.personneDAO.addPerson(entity));
	}

}
