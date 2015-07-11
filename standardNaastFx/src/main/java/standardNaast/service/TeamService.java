/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.dao.TeamDAO;
import standardNaast.dao.TeamDAOImpl;
import standardNaast.entities.Season;
import standardNaast.entities.SeasonTeam;
import standardNaast.entities.Team;
import standardNaast.model.SeasonModel;
import standardNaast.model.TeamModel;

/**
 * @author stessy
 * 
 */
public class TeamService implements Serializable {

	private final TeamDAO teamDAO = new TeamDAOImpl();

	private final SeasonDAO seasonDAO = new SeasonDAOImpl();

	private static final Logger LOGGER = LogManager
			.getLogger(TeamService.class);

	public List<TeamModel> findAllTeam() {
		final List<Team> allTeams = this.teamDAO.getAllTeams();
		final List<TeamModel> toModelTeams = allTeams.stream()
				.map(t -> TeamModel.toModel(t)).collect(Collectors.toList());
		return toModelTeams;
	}

	public List<TeamModel> getTeamsPerSeason(final SeasonModel seasonModel) {
		TeamService.LOGGER.debug("Entering getTeamsPerSeason");
		Validate.notNull(seasonModel, "Season is mandatory");
		final Season selectedSeason = this.seasonDAO.getSeasonById(seasonModel
				.getId());
		final List<SeasonTeam> teamsPerSeason = this.teamDAO
				.getTeamsPerSeason(selectedSeason);
		final List<TeamModel> toModelTeams = teamsPerSeason.stream()
				.map(t -> TeamModel.toModel(t.getOpponent())).collect(Collectors.toList());
		TeamService.LOGGER.debug("Exiting getTeamsPerSeason");
		return toModelTeams;
	}

	public void addNewTeamToSeason(final TeamModel teamModel, final SeasonModel seasonModel) {
		final Season selectedSeason = this.seasonDAO.getSeasonById(seasonModel
				.getId());
		final Team newTeam = TeamModel.toEntity(teamModel);
		final Team addedTeam = this.teamDAO.addTeam(newTeam);
		this.teamDAO.addTeamToSeason(addedTeam, selectedSeason);
	}

	public void addExistingTeamToSeason(final TeamModel teamModel, final SeasonModel seasonModel) {
		final Season selectedSeason = this.seasonDAO.getSeasonById(seasonModel
				.getId());
		final Team team = this.teamDAO.getTeam(teamModel.getId());
		this.teamDAO.addTeamToSeason(team, selectedSeason);
	}

}
