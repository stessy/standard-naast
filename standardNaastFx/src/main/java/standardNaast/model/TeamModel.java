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
		return entity;
	}

	public static TeamModel toModel(final Team entity) {
		final TeamModel model = new TeamModel();
		model.setId(entity.getEquipeId());
		model.setTeam(entity.getNomEquipe());
		return model;
	}
}
