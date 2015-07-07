package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Season;
import standardNaast.entities.SeasonTeam;
import standardNaast.entities.Team;

public interface TeamDAO {

	List<Team> getAllTeams();

	Team addTeam(Team team);

	Team updateTeam(Team team);

	List<SeasonTeam> getTeamsPerSeason(Season season);

	Team getTeam(Long id);

	void addTeamToSeason(Team team, Season season);

}
