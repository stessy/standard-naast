/**
 *
 */
package standardNaast.service;

import java.util.List;

import standardNaast.entities.Team;

/**
 * @author stessy
 * 
 */
public interface TeamService extends Service {

	<T extends List<Team>> T findAllTeam();

}
