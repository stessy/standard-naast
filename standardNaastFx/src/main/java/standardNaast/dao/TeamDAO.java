package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Season;
import standardNaast.entities.Team;

public interface TeamDAO {

	List<Team> getAllTeams();

	Team addTeam(Team team);

	Team updateTeam(Team team);

	List<Team> getTeamsPerSeason(Season season);

}
