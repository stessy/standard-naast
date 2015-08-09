package standardNaast.view.travels;

import java.util.List;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import standardNaast.model.MatchModel;
import standardNaast.model.MatchTravelsModel;
import standardNaast.model.SeasonModel;
import standardNaast.observer.SeasonObserver;
import standardNaast.service.MatchService;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;
import standardNaast.service.TravelService;
import standardNaast.types.CompetitionType;
import standardNaast.types.Place;

public class TravelsOverviewController implements SeasonObserver {

	private ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	private ObservableList<CompetitionType> competitionsList = FXCollections.observableArrayList();

	private ObservableList<Place> placesList = FXCollections.observableArrayList();

	private ObservableList<MatchModel> matchesList = FXCollections.observableArrayList();

	private ObservableList<MatchTravelsModel> travelsList = FXCollections
			.observableArrayList(new Callback<MatchTravelsModel, Observable[]>() {

				@Override
				public Observable[] call(final MatchTravelsModel param) {
					return new Observable[] { param.paidProperty() };
				}
			});

	private SeasonService seasonService = new SeasonServiceImpl();

	private MatchService matchService = new MatchService();

	private TravelService travelService = new TravelService();

	@FXML
	private TableView<MatchTravelsModel> travelsTable;

	@FXML
	private TableColumn<MatchTravelsModel, Long> memberNumberColumn;

	@FXML
	private TableColumn<MatchTravelsModel, String> firstNameColumn;

	@FXML
	private TableColumn<MatchTravelsModel, String> lastNameColumn;

	@FXML
	private TableColumn<MatchTravelsModel, Boolean> paidColumn;

	@FXML
	private TableColumn<MatchTravelsModel, Long> amountColumn;

	@FXML
	private ComboBox<SeasonModel> seasonsBox;

	@FXML
	private ComboBox<CompetitionType> competitionsTypeBox;

	@FXML
	private ComboBox<MatchModel> matchsBox;

	@FXML
	private ComboBox<Place> placesBox;

	@FXML
	private Button refreshButton;

	@FXML
	private TextField totalAmount;

	private TravelsListChangeListener travelsChangeListListener;

	public void initialize() {
		final List<SeasonModel> findAllSaison = this.seasonService.findAllSaison();
		this.update(findAllSaison);
		this.competitionsList.addAll(CompetitionType.values());
		this.competitionsTypeBox.setItems(this.competitionsList);
		this.competitionsTypeBox.setDisable(true);
		this.placesList.addAll(Place.values());
		this.placesBox.setItems(this.placesList);
		this.placesBox.setDisable(true);
		this.matchsBox.setDisable(true);
		this.refreshButton.setDisable(true);
	}

	@Override
	public void update(final List<SeasonModel> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.seasonsBox.setItems(this.seasonsList);
	}

	public void onSelectedSeason() {
		this.refreshButton.setDisable(true);
		this.competitionsTypeBox.setDisable(false);
		this.competitionsTypeBox.getSelectionModel().clearSelection();
		this.placesBox.setDisable(false);
		this.placesBox.getSelectionModel().clearSelection();
	}

	public void onSelectedCompetition() {
		this.refreshButton.setDisable(true);
		this.matchsBox.setDisable(true);
		final SeasonModel selectedSeason = this.seasonsBox.getSelectionModel().getSelectedItem();
		final CompetitionType selectedCompetitionType = this.competitionsTypeBox.getSelectionModel()
				.getSelectedItem();
		List<MatchModel> matchList;
		if (this.placesBox.getSelectionModel().isEmpty()) {
			matchList = this.matchService.getMatchListForCompetition(selectedSeason, selectedCompetitionType);
		} else {
			matchList = this.matchService.getMatchListForCompetitionWithPlaceType(selectedSeason,
					selectedCompetitionType,
					this.placesBox.getSelectionModel().getSelectedItem());

		}
		this.buildMatchList(matchList);
	}

	public void onSelectedPlace() {
		this.refreshButton.setDisable(true);
		this.matchsBox.setDisable(true);
		final SeasonModel selectedSeason = this.seasonsBox.getSelectionModel().getSelectedItem();
		final CompetitionType selectedCompetitionType = this.competitionsTypeBox.getSelectionModel()
				.getSelectedItem();
		List<MatchModel> matchList;
		matchList = this.matchService.getMatchListForCompetitionWithPlaceType(selectedSeason,
				selectedCompetitionType,
				this.placesBox.getSelectionModel().getSelectedItem());
		this.buildMatchList(matchList);
	}

	public void onSelectedMatch() {
		this.refreshButton.setDisable(false);
		final List<MatchTravelsModel> matchTravels = this.travelService.getMatchTravels(this.matchsBox
				.getSelectionModel().getSelectedItem());
		this.buildTable(matchTravels);
		this.calculateTotalAmountTravels();
	}

	private void buildTable(final List<MatchTravelsModel> matchTravels) {
		if (this.travelsChangeListListener != null) {
			this.travelsList.removeListener(this.travelsChangeListListener);
		}
		this.travelsList.clear();
		this.travelsList.addAll(matchTravels);
		this.travelsTable.setItems(this.travelsList);
		this.travelsChangeListListener = new TravelsListChangeListener();
		this.travelsList.addListener(this.travelsChangeListListener);
		this.bindProperties();
	}

	private void bindProperties() {
		this.memberNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getPerson().memberNumberProperty()
				.asObject());
		this.firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPerson().firstNameProperty());
		this.lastNameColumn.setCellValueFactory(celldata -> celldata.getValue().getPerson().nameProperty());
		this.paidColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());
		this.amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		this.paidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.paidColumn));
	}

	private void buildMatchList(final List<MatchModel> matchList) {
		this.matchesList.clear();
		this.matchesList.addAll(matchList);
		this.matchsBox.setItems(this.matchesList);
		this.matchsBox.setDisable(false);
	}

	private class TravelsListChangeListener implements ListChangeListener<MatchTravelsModel> {

		@Override
		public void onChanged(final javafx.collections.ListChangeListener.Change<? extends MatchTravelsModel> c) {
			while (c.next()) {
				if (c.wasUpdated()) {
					final MatchTravelsModel updatedTravel = TravelsOverviewController.this.travelsList.get(c.getFrom());
					if (updatedTravel.isPaid()) {
						final MatchTravelsModel addedMemberMatchTravel = TravelsOverviewController.this.travelService
								.addMemberMatchTravel(updatedTravel);
						TravelsOverviewController.this.travelsList.set(c.getFrom(), addedMemberMatchTravel);
					} else {
						TravelsOverviewController.this.travelService
								.removeMemberMatchTravel(updatedTravel);
					}
				}
			}

			TravelsOverviewController.this.calculateTotalAmountTravels();
		}

	}

	private void calculateTotalAmountTravels() {
		Long totalAmounTravels = new Long(0);
		for (final MatchTravelsModel matchTravelsModel : TravelsOverviewController.this.travelsList) {
			if (matchTravelsModel.isPaid()) {
				totalAmounTravels += matchTravelsModel.getAmount();
			}
		}
		this.totalAmount.setText(String.valueOf(totalAmounTravels) + " €");
	}

	public void onRefresh() {
		this.onSelectedMatch();
	}
}
