/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.dao.TeamDAO;
import standardNaast.entities.Team;

/**
 * @author stessy
 * 
 */
@Stateless
@LocalBean
public class TeamService implements Serializable {

	@Inject
	TeamDAO TeamDAO;

	private static final Logger LOGGER = Logger
			.getLogger(PersonneServiceImpl.class);

	public List<Team> findAllTeam() {
		return this.TeamDAO.getAllTeams();
	}

}
