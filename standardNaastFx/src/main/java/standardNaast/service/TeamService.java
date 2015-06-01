/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.dao.TeamDAO;
import standardNaast.dao.TeamDAOImpl;
import standardNaast.entities.Team;

/**
 * @author stessy
 * 
 */
public class TeamService implements Serializable {

	private final TeamDAO TeamDAO = new TeamDAOImpl();

	private static final Logger LOGGER = LogManager.getLogger(PersonneServiceImpl.class);

	public List<Team> findAllTeam() {
		return this.TeamDAO.getAllTeams();
	}

}
