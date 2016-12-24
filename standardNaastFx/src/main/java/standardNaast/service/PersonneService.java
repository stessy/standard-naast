/**
 *
 */
package standardNaast.service;

import java.util.List;

import standardNaast.model.PersonModel;

/**
 * @author stessy
 *
 */
public interface PersonneService {

	public List<PersonModel> findAllPerson(final boolean allPersons);

	public PersonModel getPerson(final long id);

	public PersonModel getPersonByMemberNumber(final long memberNumber);

	public PersonModel savePerson(final PersonModel person);

	public PersonModel addPerson(final PersonModel person);

	Long getMaxMemberNumber();

	void deletePerson(long id);

}
