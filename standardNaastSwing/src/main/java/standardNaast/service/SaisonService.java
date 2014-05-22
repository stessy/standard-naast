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
public interface SaisonService extends Service {

	<T extends List<Season>> T findAllSaison();

}
