/**
 *
 */
package standardNaast.service;

import java.util.List;

import standardNaast.entities.Personne;

/**
 * @author stessy
 *
 */
public interface PersonneService {

	public List<Personne> findAllPerson(final boolean allPersons);

	public Personne getPerson(final long id);

	public Personne getPersonByMemberNumber(final long memberNumber);

	public Personne savePerson(final Personne person);

	public Personne addPerson(final Personne person);

}
