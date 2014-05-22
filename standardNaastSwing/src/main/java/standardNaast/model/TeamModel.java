/**
 * 
 */
package standardNaast.model;

import java.beans.PropertyChangeSupport;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

import standardNaast.entities.Team;
import standardNaast.service.TeamService;
import standardNaast.service.TeamServiceImpl;

/**
 * @author stessy
 * 
 */
public class TeamModel {

	private ObservableList<Team> teamList;

	private Team team;

	private TeamService teamService;

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


	public ObservableList<Team> getTeamsList() {
		if (CollectionUtils.isEmpty(this.teamList)) {
			if (this.teamService == null) {
				this.teamService = new TeamServiceImpl();
			}
			this.teamList = ObservableCollections.observableList(this.teamService.findAllTeam());
		}
		Collections.sort(this.teamList);
		return this.teamList;
	}


	public Team getSelectedTeam() {
		return this.team;
	}


	public void setSelectedTeam(final Team team) {
		Team oldTeam = this.team;
		this.team = team;
		this.changeSupport.firePropertyChange("team", oldTeam, this.team);
	}
}
