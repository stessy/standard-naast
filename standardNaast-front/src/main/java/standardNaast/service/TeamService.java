/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.dao.TeamDAO;
import standardNaast.entities.Team;

/**
 * @author stessy
 * 
 */
public class TeamService implements Serializable {

	// @Autowired
	@Inject
	TeamDAO TeamDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneService.class);

	public List<Team> findAllTeam() {
		return this.TeamDAO.getAllTeams();
	}

}
