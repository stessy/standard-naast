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
public class PersonneServiceImpl extends AbstractService implements PersonneService {

	@Override
	public <T extends List<Personne>> T findAllPerson() {
		return (T) this.abstractGenericDAO.findAll(Personne.class);
		// return (T) new ArrayList<Personne>();
	}


	@Override
	public Personne getPerson(final long id) {
		return this.abstractGenericDAO.findByPrimaryKey(Personne.class, id);
	}

}
