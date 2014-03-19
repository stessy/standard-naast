/**
 *
 */
package standardNaast.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import standardNaast.dao.TeamDAO;
import standardNaast.entities.Team;

/**
 * @author stessy
 * 
 */
public class TeamService {

	@Autowired
	TeamDAO TeamDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneService.class);

	public List<Team> findAllTeam() {
		return this.TeamDAO.getAllTeams();
	}

}
