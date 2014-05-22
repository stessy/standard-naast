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
public class TeamServiceImpl extends AbstractService implements TeamService {

	@Override
	public <T extends List<Team>> T findAllTeam() {
		return (T) this.abstractGenericDAO.findAll(Team.class);
	}

}
