/**
 *
 */
package standardNaast.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.entities.Personne;
import standardNaast.model.PersonModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PersonneServiceImpl implements PersonneService {

    PersonDAO personneDAO = new PersonDAOImpl();

    private static final Logger LOGGER = LogManager
            .getLogger(PersonneServiceImpl.class);

    public PersonneServiceImpl() {
        PersonneServiceImpl.LOGGER.debug("Instanciating PersonneService");
    }

    @Override
    public List<PersonModel> findAllPerson(final boolean includeNonMembers) {
        PersonneServiceImpl.LOGGER.debug("Getting list of personnes");
        final List<Personne> allPersons = this.personneDAO.getAllPersons(includeNonMembers);
        final List<PersonModel> personsModel = allPersons.stream().map(PersonModel::toModel)
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
        entity.setMemberNumber(1000);
        return PersonModel.toModel(this.personneDAO.addPerson(entity));
    }

    @Override
    public Long getMaxMemberNumber() {
        return this.personneDAO.getMaxMemberNumber();
    }

    @Override
    public void deletePerson(final long id) {
        final Personne person = this.personneDAO.getPerson(id);
        person.setMemberNumber(20000);
        this.personneDAO.updatePerson(person);
        final List<Personne> allPersons = this.personneDAO.getAllPersons(false);
        Collections.sort(allPersons);
        int memberNumber = 1;
        for (final Personne personne : allPersons) {
            personne.setMemberNumber(memberNumber++);
            this.personneDAO.updatePerson(personne);
        }

    }

}
