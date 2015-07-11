package standardNaast.view.season;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import standardNaast.model.MatchModel;
import standardNaast.model.SeasonModel;
import standardNaast.model.TeamModel;
import standardNaast.service.MatchService;
import standardNaast.service.TeamService;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;
import standardNaast.types.PriceType;
import standardNaast.utils.AlertDialogUtils;

public class MatchFormController {

	private final MatchService matchService = new MatchService();

	private final TeamService teamService = new TeamService();

	private final List<String> validationErrors = new ArrayList<>();

	private final ObservableList<CompetitionType> competitionTypes = FXCollections.observableArrayList();

	private final ObservableList<MatchType> matchTypes = FXCollections.observableArrayList();

	private final ObservableList<PriceType> priceTypes = FXCollections.observableArrayList();

	private final ObservableList<Place> places = FXCollections.observableArrayList();

	private final ObservableList<TeamModel> teams = FXCollections.observableArrayList();

	@FXML
	private ComboBox<TeamModel> opponent;

	@FXML
	private ComboBox<CompetitionType> competitionType;

	@FXML
	private ComboBox<Place> place;

	@FXML
	private ComboBox<PriceType> priceType;

	@FXML
	private ComboBox<MatchType> matchTypeBox;

	@FXML
	private DatePicker matchDate;

	@FXML
	private Button addButton;

	@FXML
	private Button cancelButton;

	private Stage matchDialog;

	private SeasonModel selectedSeason;

	private SeasonMatchController controller;

	public void initialize() {
		this.matchTypeBox.setDisable(true);
		this.matchDate.setEditable(false);
		this.competitionTypes.addAll(CompetitionType.values());
		this.competitionType.setItems(this.competitionTypes);
		this.matchTypes.addAll(MatchType.values());
		this.matchTypeBox.setItems(this.matchTypes);
		this.priceTypes.addAll(PriceType.values());
		this.priceType.setItems(this.priceTypes);
		this.places.addAll(Place.values());
		this.place.setItems(this.places);
	}

	public void onSelectedCompetition() {
		final CompetitionType selectedCompetition = this.competitionType.getSelectionModel().getSelectedItem();
		final MatchType[] matchsType = selectedCompetition.getMatchType();
		this.matchTypes.clear();
		if (matchsType.length > 0) {
			this.matchTypes.addAll(matchsType);
			this.matchTypeBox.setItems(this.matchTypes);
			this.matchTypeBox.setDisable(false);
		}
		else {
			this.matchTypeBox.setDisable(true);
		}
	}

	public void onCancel() {
		this.matchDialog.close();
	}

	public void onAdd() {
		if (this.isValid()) {
			final MatchModel model = new MatchModel();
			model.setCompetitionType(this.competitionType.getSelectionModel().getSelectedItem());
			model.setMatchDate(this.matchDate.getValue());
			model.setMatchType(this.matchTypeBox.getSelectionModel().getSelectedItem());
			model.setOpponent(this.opponent.getSelectionModel().getSelectedItem());
			model.setPlace(this.place.getSelectionModel().getSelectedItem());
			model.setPriceType(this.priceType.getSelectionModel().getSelectedItem());
			model.setSeason(this.selectedSeason);
			final MatchModel addedMatch = this.matchService.addMatch(model);
			this.controller.onAddedMatch(addedMatch);
			this.matchDialog.close();
			AlertDialogUtils.displaySuccessALert("Match ajouté");
		} else {
			AlertDialogUtils.displayInvalidAlert(this.matchDialog, this.validationErrors);
		}
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateOpponent();
		this.validateCompetitionType();
		this.validatePlace();
		this.validatePriceType();
		this.validateMatchType();
		this.validateMatchDate();
		return this.validationErrors.isEmpty();
	}

	private void validateOpponent() {
		if (this.opponent.getSelectionModel().isEmpty()) {
			this.validationErrors.add("Adversaire non sélectionné");
		}
	}

	private void validatePlace() {
		if (this.place.getSelectionModel().isEmpty()) {
			this.validationErrors.add("Lieu non sélectionné");
		}
	}

	private void validateCompetitionType() {
		if (this.competitionType.getSelectionModel().isEmpty()) {
			this.validationErrors.add("Compétition non sélectionné");
		}
	}

	private void validateMatchType() {
		final boolean emptyMatchTypes = this.matchTypeBox.getItems().isEmpty();
		if (!emptyMatchTypes && this.matchTypeBox.getSelectionModel().isEmpty()) {
			this.validationErrors.add("Type de match non sélectionné");
		}
	}

	private void validatePriceType() {
		if (this.priceType.getSelectionModel().isEmpty()) {
			this.validationErrors.add("Type de prix non sélectionné");
		}
	}

	private void validateMatchDate() {
		final LocalDate value = this.matchDate.getValue();
		if (value == null) {
			this.validationErrors.add("Date de match non sélectionné");
		}
	}

	public void setSeason(final SeasonModel selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public void setParentController(final SeasonMatchController seasonMatchController) {
		this.controller = seasonMatchController;
	}

	public void setStage(final Stage matchDialog) {
		this.matchDialog = matchDialog;
	}

	public void fillTeams() {
		final List<TeamModel> teamsPerSeason = this.teamService.getTeamsPerSeason(this.selectedSeason);
		this.teams.clear();
		this.teams.addAll(teamsPerSeason);
		this.opponent.setItems(this.teams);
	}
}
