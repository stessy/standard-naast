package standardNaast.view.travels;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

import standardNaast.model.MatchModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PersonTravelModel;
import standardNaast.model.PersonsMatchTravel;
import standardNaast.model.SeasonModel;
import standardNaast.observer.SeasonObserver;
import standardNaast.service.MatchService;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;
import standardNaast.service.TravelService;
import standardNaast.types.CompetitionType;
import standardNaast.types.Place;

public class TravelsOverviewController implements SeasonObserver {

	private AutoCompletionBinding<PersonModel> autoCompletionBinding;

	private ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	private ObservableList<CompetitionType> competitionsList = FXCollections.observableArrayList();

	private ObservableList<Place> placesList = FXCollections.observableArrayList();

	private ObservableList<MatchModel> matchesList = FXCollections.observableArrayList();

	private ObservableList<PersonTravelModel> membersTravelList = FXCollections
			.observableArrayList(new Callback<PersonTravelModel, Observable[]>() {

				@Override
				public Observable[] call(final PersonTravelModel param) {
					return new Observable[] { param.paidProperty() };
				}
			});

	private ObservableList<PersonTravelModel> nonMembersTravelList = FXCollections
			.observableArrayList(new Callback<PersonTravelModel, Observable[]>() {

				@Override
				public Observable[] call(final PersonTravelModel param) {
					return new Observable[] { param.paidProperty() };
				}
			});

	private SeasonService seasonService = new SeasonServiceImpl();

	private MatchService matchService = new MatchService();

	private TravelService travelService = new TravelService();

	@FXML
	private TableView<PersonTravelModel> memberTravelsTable;

	@FXML
	private TableView<PersonTravelModel> nonMemberTravelsTable;

	@FXML
	private TableColumn<PersonTravelModel, Long> memberNumberColumn;

	@FXML
	private TableColumn<PersonTravelModel, String> memberFirstNameColumn;

	@FXML
	private TableColumn<PersonTravelModel, String> memberLastNameColumn;

	@FXML
	private TableColumn<PersonTravelModel, Boolean> memberPaidColumn;

	@FXML
	private TableColumn<PersonTravelModel, Long> memberAmountColumn;

	@FXML
	private TableColumn<PersonTravelModel, String> nonMemberFirstNameColumn;

	@FXML
	private TableColumn<PersonTravelModel, String> nonMemberLastNameColumn;

	@FXML
	private TableColumn<PersonTravelModel, Boolean> nonMemberPaidColumn;

	@FXML
	private TableColumn<PersonTravelModel, Long> nonMemberAmountColumn;

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

	private MembersTravelsListChangeListener membersTravelsListChangeListener;

	private NonMembersTravelsListChangeListener nonMembersTravelsListChangeListener;

	@FXML
	private TextField nonMemberField;

	@FXML
	private TextField travelAmount;

	@FXML
	private DatePicker nonMemberBirthDate;

	@FXML
	private Button addTravelButton;

	private PersonModel selectedUnpaidNonMember;

	private MatchModel selectedMatch;

	private List<PersonModel> unpaidNonMembers;

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
		this.addTravelButton.setDisable(true);
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
		this.selectedMatch = this.matchsBox
				.getSelectionModel().getSelectedItem();
		final PersonsMatchTravel matchTravels = this.travelService.getMatchTravels(this.selectedMatch);
		this.buildMembersTable(matchTravels);
		this.buildNonMembersTable(matchTravels);
		this.unpaidNonMembers = matchTravels.getUnpaidNonMembers();
		this.buildAutoCompleteField();
		this.calculateTotalAmountTravels();
	}

	private void buildMembersTable(final PersonsMatchTravel matchTravels) {
		if (this.membersTravelsListChangeListener != null) {
			this.membersTravelList.removeListener(this.membersTravelsListChangeListener);
		}
		this.membersTravelList.clear();
		this.membersTravelList.addAll(matchTravels.getMembers());
		this.memberTravelsTable.setItems(this.membersTravelList);
		this.membersTravelsListChangeListener = new MembersTravelsListChangeListener();
		this.membersTravelList.addListener(this.membersTravelsListChangeListener);
		this.bindMembersProperties();
	}

	private void buildNonMembersTable(final PersonsMatchTravel matchTravels) {
		if (this.nonMembersTravelsListChangeListener != null) {
			this.nonMembersTravelList.removeListener(this.nonMembersTravelsListChangeListener);
		}
		this.nonMembersTravelList.clear();
		this.nonMembersTravelList.addAll(matchTravels.getNonMembers());
		this.nonMemberTravelsTable.setItems(this.nonMembersTravelList);
		this.nonMembersTravelsListChangeListener = new NonMembersTravelsListChangeListener();
		this.nonMembersTravelList.addListener(this.nonMembersTravelsListChangeListener);
		this.bindNonMembersProperties();
	}

	private void buildAutoCompleteField() {
		// we dispose the old binding and recreate a new binding
		if (this.autoCompletionBinding != null) {
			this.autoCompletionBinding.dispose();
		}
		this.autoCompletionBinding = TextFields.bindAutoCompletion(this.nonMemberField, this.unpaidNonMembers);

		this.autoCompletionBinding
				.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<PersonModel>>() {

					@Override
					public void handle(final AutoCompletionEvent<PersonModel> event) {
						TravelsOverviewController.this.onSelectedNonMember(event.getCompletion());
					}
				});
	}

	private void bindMembersProperties() {
		this.memberNumberColumn.setCellValueFactory(cellData -> cellData.getValue().getPerson().memberNumberProperty()
				.asObject());
		this.memberFirstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPerson().firstNameProperty());
		this.memberLastNameColumn.setCellValueFactory(celldata -> celldata.getValue().getPerson().nameProperty());
		this.memberPaidColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());
		this.memberAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		this.memberPaidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.memberPaidColumn));
	}

	private void bindNonMembersProperties() {
		this.nonMemberFirstNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPerson()
				.firstNameProperty());
		this.nonMemberLastNameColumn.setCellValueFactory(celldata -> celldata.getValue().getPerson().nameProperty());
		this.nonMemberPaidColumn.setCellValueFactory(cellData -> cellData.getValue().paidProperty());
		this.nonMemberAmountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		this.nonMemberPaidColumn.setCellFactory(CheckBoxTableCell.forTableColumn(this.nonMemberPaidColumn));
	}

	private void buildMatchList(final List<MatchModel> matchList) {
		this.matchesList.clear();
		this.matchesList.addAll(matchList);
		this.matchsBox.setItems(this.matchesList);
		this.matchsBox.setDisable(false);
	}

	private class MembersTravelsListChangeListener implements ListChangeListener<PersonTravelModel> {

		@Override
		public void onChanged(final javafx.collections.ListChangeListener.Change<? extends PersonTravelModel> c) {
			while (c.next()) {
				if (c.wasUpdated()) {
					final PersonTravelModel updatedTravel = TravelsOverviewController.this.membersTravelList.get(c
							.getFrom());
					if (updatedTravel.isPaid()) {
						final PersonTravelModel addedMemberMatchTravel = TravelsOverviewController.this.travelService
								.addMemberMatchTravel(updatedTravel);
						TravelsOverviewController.this.membersTravelList.set(c.getFrom(), addedMemberMatchTravel);
					} else {
						TravelsOverviewController.this.travelService
								.removeMemberMatchTravel(updatedTravel);
					}
				}
			}

			TravelsOverviewController.this.calculateTotalAmountTravels();
		}

	}

	private class NonMembersTravelsListChangeListener implements ListChangeListener<PersonTravelModel> {

		@Override
		public void onChanged(final javafx.collections.ListChangeListener.Change<? extends PersonTravelModel> c) {
			while (c.next()) {
				if (c.wasUpdated()) {
					final PersonTravelModel updatedTravel = TravelsOverviewController.this.nonMembersTravelList.get(c
							.getFrom());
					if (updatedTravel.isPaid()) {
						final PersonTravelModel addedNonMemberMatchTravel = TravelsOverviewController.this.travelService
								.addMemberMatchTravel(updatedTravel);
						TravelsOverviewController.this.nonMembersTravelList.set(c.getFrom(), addedNonMemberMatchTravel);
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
		for (final PersonTravelModel personTravelModel : TravelsOverviewController.this.membersTravelList) {
			if (personTravelModel.isPaid()) {
				totalAmounTravels += personTravelModel.getAmount();
			}
		}
		for (final PersonTravelModel personTravelModel : TravelsOverviewController.this.nonMembersTravelList) {
			if (personTravelModel.isPaid()) {
				totalAmounTravels += personTravelModel.getAmount();
			}
		}
		this.totalAmount.setText(String.valueOf(totalAmounTravels) + " €");
	}

	public void onRefresh() {
		this.onSelectedMatch();
	}

	public void onSelectedNonMember(final PersonModel nonMemberSelected) {
		final LocalDate birthDate = nonMemberSelected.getBirthdate();
		this.nonMemberBirthDate.setValue(birthDate);
		this.selectedUnpaidNonMember = nonMemberSelected;
		final Long travelAmount = this.travelService.getMemberTravelPriceForMatch(nonMemberSelected,
				this.selectedMatch,
				this.travelService.getTravelsPricePerSeason(this.seasonsBox.getSelectionModel().getSelectedItem()));
		this.travelAmount.setText(String.valueOf(travelAmount + " €"));
		this.addTravelButton.setDisable(false);
	}

	public void onAddMember() {
		final PersonTravelModel addNonMemberMatchTravel = this.travelService.addNonMemberMatchTravel(
				this.selectedUnpaidNonMember,
				this.selectedMatch, this.seasonsBox
						.getSelectionModel().getSelectedItem());
		this.removeNonMemberFromSuggestion();
		this.nonMembersTravelList.add(addNonMemberMatchTravel);
		// this.nonMemberField.setText(null);
		this.nonMemberBirthDate.setValue(null);
		this.travelAmount.setText(null);
		this.addTravelButton.setDisable(true);
	}

	private void removeNonMemberFromSuggestion() {
		this.unpaidNonMembers.remove(this.selectedUnpaidNonMember);

		this.buildAutoCompleteField();
	}

}
