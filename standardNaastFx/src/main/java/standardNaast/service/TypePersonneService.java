/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.dao.TypePersonneDAO;
import standardNaast.entities.TypePersonne;

/**
 * @author stessy
 * 
 */
public class TypePersonneService implements Serializable {

	// @Autowired
	@Inject
	TypePersonneDAO typePersonnesDAO;

	private static final Logger LOGGER = LogManager
			.getLogger(PersonneServiceImpl.class);

	public List<TypePersonne> findAllTypePersonnes() {
		return this.typePersonnesDAO.getAllTypePersonne();
	}

}
