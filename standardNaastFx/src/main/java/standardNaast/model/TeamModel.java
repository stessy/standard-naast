package standardNaast.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.entities.Team;

public class TeamModel {

	private final LongProperty id = new SimpleLongProperty();

	private final StringProperty team = new SimpleStringProperty();

	public Long getId() {
		return this.id.get();
	}

	public void setId(final Long id) {
		this.id.set(id);
	}

	public String getTeam() {
		return this.team.get();
	}

	public void setTeam(final String team) {
		this.team.set(team);
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public StringProperty teamProperty() {
		return this.team;
	}

	public static Team toEntity(final TeamModel model) {
		final Team entity = new Team();
		entity.setNomEquipe(model.getTeam());
		entity.setEquipeId(model.getId());
		return entity;
	}

	public static TeamModel toModel(final Team entity) {
		final TeamModel model = new TeamModel();
		model.setId(entity.getEquipeId());
		model.setTeam(entity.getNomEquipe());
		return model;
	}

	@Override
	public String toString() {
		return this.getTeam();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
		result = prime * result + ((this.getTeam() == null) ? 0 : this.getTeam().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final TeamModel other = (TeamModel) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		if (this.getTeam() == null) {
			if (other.getTeam() != null) {
				return false;
			}
		} else if (!this.getTeam().equals(other.getTeam())) {
			return false;
		}
		return true;
	}
}
