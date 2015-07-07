package standardNaast.view.season;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.apache.commons.lang3.StringUtils;

import standardNaast.model.SeasonModel;
import standardNaast.model.TeamModel;
import standardNaast.service.TeamService;
import standardNaast.utils.AlertDialogUtils;

public class TeamSeasonController {

	private final TeamService teamService = new TeamService();

	private ObservableList<InternalTeamModel> teamsList = FXCollections.observableArrayList();

	private ObservableList<TeamModel> allTeams = FXCollections.observableArrayList();

	@FXML
	private TableView<InternalTeamModel> teamsTable;

	@FXML
	private TableColumn<InternalTeamModel, String> col1;

	@FXML
	private TableColumn<InternalTeamModel, String> col2;

	@FXML
	private TableColumn<InternalTeamModel, String> col3;

	@FXML
	private TableColumn<InternalTeamModel, String> col4;

	@FXML
	private ComboBox<TeamModel> availableTeams;

	@FXML
	private CheckBox notFoundTeam;

	@FXML
	private TextField newTeam;

	@FXML
	private Button addTeamButton;

	private SeasonModel selectedSeason;

	private final List<String> validationErrors = new ArrayList<>();

	@FXML
	public void initialize() {
		this.newTeam.setDisable(true);
	}

	public void onSelectedSeason(final SeasonModel selectedSeason) {
		this.selectedSeason = selectedSeason;
		final List<TeamModel> teamsPerSeason = this.teamService.getTeamsPerSeason(selectedSeason);
		final List<TeamModel> allTeams = this.teamService.findAllTeam();
		allTeams.removeAll(teamsPerSeason);
		this.buildTable(teamsPerSeason);
		this.buildAvailableTeams(allTeams);
	}

	private void buildTable(final List<TeamModel> teams) {
		final List<InternalTeamModel> convertToInternalModel = this.convertToInternalModel(teams);
		this.teamsList.clear();
		this.teamsList.addAll(convertToInternalModel);
		this.teamsTable.setItems(this.teamsList);
		this.bindProperties();
	}

	private void buildAvailableTeams(final List<TeamModel> teams) {
		this.allTeams.clear();
		this.allTeams.addAll(teams);
		this.availableTeams.setItems(this.allTeams);
	}

	private List<InternalTeamModel> convertToInternalModel(final List<TeamModel> teams) {
		final List<InternalTeamModel> internalModel = new ArrayList<>();
		final int splitterSize = 4;
		int counter = 0;
		final int originalListSize = teams.size();
		while (counter < originalListSize) {
			final int counterAndSplitter = counter + splitterSize;
			final List<TeamModel> subList = teams.subList(counter, Math.min(counterAndSplitter, originalListSize));
			counter += splitterSize;
			final InternalTeamModel internalTeamModel = new InternalTeamModel();
			switch (subList.size()) {
			case 4:
				internalTeamModel.setCol4(subList.get(3).getTeam());
			case 3:
				internalTeamModel.setCol3(subList.get(2).getTeam());
			case 2:
				internalTeamModel.setCol2(subList.get(1).getTeam());
			case 1:
				internalTeamModel.setCol1(subList.get(0).getTeam());
				break;
			default:
			}
			internalModel.add(internalTeamModel);
		}
		return internalModel;
	}

	public void onCheckBoxChange() {
		if (this.notFoundTeam.isSelected()) {
			this.availableTeams.setDisable(true);
			this.newTeam.setDisable(false);
		} else {
			this.availableTeams.setDisable(false);
			this.newTeam.setDisable(true);
		}
	}

	public void onAddTeam() {
		if (this.isValid()) {
			final boolean newTeamSelected = this.notFoundTeam.isSelected();
			if (newTeamSelected) {
				final TeamModel teamModel = new TeamModel();
				teamModel.setTeam(this.newTeam.getText());
				this.teamService.addNewTeamToSeason(teamModel, this.selectedSeason);
			} else {
				final TeamModel selectedTeam = this.availableTeams.getSelectionModel().getSelectedItem();
				this.teamService.addExistingTeamToSeason(selectedTeam, this.selectedSeason);
			}
			this.newTeam.setText(StringUtils.EMPTY);
			this.notFoundTeam.setSelected(false);
			this.onCheckBoxChange();
			this.onSelectedSeason(this.selectedSeason);
		} else {
			AlertDialogUtils.displayInvalidAlert(null, this.validationErrors);
		}
	}

	private boolean isValid() {
		this.validationErrors.clear();
		final String error = "Aucune équipé sélectionnée";
		if (this.notFoundTeam.isSelected() && StringUtils.isEmpty(this.newTeam.getText())) {
			this.validationErrors.add(error);
		} else if (!this.notFoundTeam.isSelected()) {
			final int selectedTeam = this.availableTeams.getSelectionModel().getSelectedIndex();
			if (selectedTeam < 0) {
				this.validationErrors.add(error);
			}
		}
		if (!this.validationErrors.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private void bindProperties() {
		this.col1.setCellValueFactory(cellData -> cellData.getValue().col1Property());
		this.col2.setCellValueFactory(cellData -> cellData.getValue().col2Property());
		this.col3.setCellValueFactory(cellData -> cellData.getValue().col3Property());
		this.col4.setCellValueFactory(cellData -> cellData.getValue().col4Property());
	}

	private class InternalTeamModel {
		private StringProperty col1 = new SimpleStringProperty();

		private StringProperty col2 = new SimpleStringProperty();

		private StringProperty col3 = new SimpleStringProperty();

		private StringProperty col4 = new SimpleStringProperty();

		public void setCol1(final String col1) {
			this.col1.set(col1);
		}

		public void setCol2(final String col2) {
			this.col2.set(col2);
		}

		public void setCol3(final String col3) {
			this.col3.set(col3);
		}

		public void setCol4(final String col4) {
			this.col4.set(col4);
		}

		public StringProperty col1Property() {
			return this.col1;
		}

		public StringProperty col2Property() {
			return this.col2;
		}

		public StringProperty col3Property() {
			return this.col3;
		}

		public StringProperty col4Property() {
			return this.col4;
		}

	}

}
