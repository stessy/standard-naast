/**
 *
 */
package standardNaast.service;

import java.util.List;

import standardNaast.entities.Season;

/**
 * @author stessy
 * 
 */
public class SaisonServiceImpl extends AbstractService implements SaisonService {

	@Override
	public <T extends List<Season>> T findAllSaison() {
		return (T) this.abstractGenericDAO.findAll(Season.class);
	}

}
