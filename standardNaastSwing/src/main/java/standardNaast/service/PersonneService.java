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
public interface PersonneService extends Service {

	<T extends List<Personne>> T findAllPerson();


	Personne getPerson(long id);

}
