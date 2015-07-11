package standardNaast.view.season;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.MatchModel;
import standardNaast.model.SeasonModel;
import standardNaast.model.TeamModel;
import standardNaast.service.MatchService;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;

public class SeasonMatchController {

	private final MatchService matchService = new MatchService();

	private ObservableList<MatchModel> matchsList = FXCollections.observableArrayList();

	private final ObservableList<CompetitionType> competitionTypes = FXCollections.observableArrayList();

	private final ObservableList<MatchType> matchTypes = FXCollections.observableArrayList();

	@FXML
	private TableView<MatchModel> matchsTable;

	@FXML
	private TableColumn<MatchModel, TeamModel> opponent;

	@FXML
	private TableColumn<MatchModel, Place> place;

	@FXML
	private TableColumn<MatchModel, LocalDate> matchDate;

	@FXML
	private ComboBox<CompetitionType> competitionTypeBox;

	@FXML
	private ComboBox<MatchType> matchTypeBox;

	@FXML
	private Button addMatchButton;

	@FXML
	private Button editMatchButton;

	private MatchModel selectedMatch;

	private SeasonModel selectedSeason;

	@FXML
	public void initialize() {
		this.competitionTypes.clear();
		this.competitionTypes.addAll(CompetitionType.values());
		this.competitionTypeBox.setItems(this.competitionTypes);
		this.competitionTypeBox.setDisable(true);
		this.matchTypeBox.setDisable(true);
		this.addMatchButton.setDisable(true);
		this.editMatchButton.setDisable(true);
	}

	public void onSelectedSeason(final SeasonModel selectedSeason) {
		this.selectedSeason = selectedSeason;
		this.competitionTypeBox.setDisable(false);
		this.addMatchButton.setDisable(false);
		if (!this.competitionTypeBox.getSelectionModel().isEmpty()) {
			if (this.matchTypeBox.getSelectionModel().isEmpty()) {
				this.buildTable(false);
			} else {
				this.buildTable(true);
			}
		}
	}

	private void buildTable(final boolean withMatchType) {
		final List<MatchModel> matchsFound;
		final CompetitionType selectedCompetition = this.competitionTypeBox.getSelectionModel().getSelectedItem();
		if (withMatchType) {
			final MatchType selectedMatchType = this.matchTypeBox.getSelectionModel().getSelectedItem();
			matchsFound = this.matchService.getMatchListForCompetitionWithMatchType(this.selectedSeason, selectedCompetition,
					selectedMatchType);
		} else {
			matchsFound = this.matchService.getMatchListForCompetition(this.selectedSeason,
					selectedCompetition);
		}
		this.matchsList.clear();
		this.matchsList.addAll(matchsFound);
		this.matchsTable.setItems(this.matchsList);
		this.bindProperties();
	}

	private void bindProperties() {
		this.opponent.setCellValueFactory(cellData -> cellData.getValue().opponentProperty());
		this.place.setCellValueFactory(cellData -> cellData.getValue().placeProperty());
		this.matchDate.setCellValueFactory(cellData -> cellData.getValue().matchDateProperty());
	}

	public void onSelectedCompetitionType() {
		final CompetitionType selectedCompetition = this.competitionTypeBox.getSelectionModel().getSelectedItem();
		final MatchType[] matchTypesForCompetition = selectedCompetition.getMatchType();
		this.matchTypes.clear();
		if (matchTypesForCompetition.length > 0) {
			this.matchTypes.addAll(matchTypesForCompetition);
			this.matchTypeBox.setItems(this.matchTypes);
			this.matchTypeBox.setDisable(false);
		}
		else {
			this.matchTypeBox.setDisable(true);
		}
		this.buildTable(false);
	}

	public void onSelectedTypeMatch() {
		this.buildTable(true);
	}

	public void onAddMatch() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MatchFormController.class.getResource("MatchForm.fxml"));
			final GridPane pane = (GridPane) loader.load();
			final MatchFormController controller = (MatchFormController) loader.getController();
			controller.setSeason(this.selectedSeason);
			controller.fillTeams();
			controller.setParentController(this);
			final Stage matchDialog = new Stage();
			matchDialog.setTitle("Match");
			matchDialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			matchDialog.setScene(scene);
			controller.setStage(matchDialog);
			matchDialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void onAddedMatch(final MatchModel addedMatch) {
		this.matchsList.add(addedMatch);
		this.matchsTable.setItems(this.matchsList);
	}
}
