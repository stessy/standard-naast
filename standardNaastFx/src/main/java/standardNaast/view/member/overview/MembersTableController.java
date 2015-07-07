package standardNaast.view.member.overview;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import standardNaast.model.PersonModel;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

public class MembersTableController {

	private final ObservableList<PersonModel> memberList = FXCollections.observableArrayList();

	private final PersonneService personneService = new PersonneServiceImpl();

	@FXML
	private TableView<PersonModel> membersTable;

	@FXML
	private TableColumn<PersonModel, Long> memberNumberColumn;

	@FXML
	private TableColumn<PersonModel, String> memberNameColumn;

	@FXML
	private TableColumn<PersonModel, String> firstNameMemberColumn;

	@FXML
	private TableColumn<PersonModel, String> addressMemberColumn;

	@FXML
	private TableColumn<PersonModel, String> postalCodeMemberColumn;

	@FXML
	private TableColumn<PersonModel, String> cityMemberColumn;

	@FXML
	private TableColumn<PersonModel, String> phoneNumberMemberColumn;

	@FXML
	private TableColumn<PersonModel, String> mobilePhoneMemberColumn;

	private final NameHeaderColumnFiltering nameHeaderColumnFiltering = new NameHeaderColumnFiltering();

	@FXML
	private void initialize() {
		this.buildModel();
	}

	public void buildModel() {
		final List<PersonModel> allMembers = this.personneService.findAllPerson(false);
		this.memberList.clear();
		for (final PersonModel model : allMembers) {
			this.memberList.add(model);
		}

		this.membersTable.setItems(this.memberList);
		this.bindProperties();
		this.installFiltering();
	}

	private void bindProperties() {
		this.memberNumberColumn.setCellValueFactory(cellData -> cellData.getValue().memberNumberProperty().asObject());
		this.firstNameMemberColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
		this.memberNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		this.addressMemberColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
		this.postalCodeMemberColumn.setCellValueFactory(cellData -> cellData.getValue().postalCodeProperty());
		this.cityMemberColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
		this.phoneNumberMemberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
		this.mobilePhoneMemberColumn.setCellValueFactory(cellData -> cellData.getValue().mobilePhoneProperty());
		this.memberNameColumn.setGraphic(this.nameHeaderColumnFiltering);
	}

	private class NameHeaderColumnFiltering extends VBox {
		private final Label nameLabel = new Label("Nom");

		private final TextField nameFieldFiltering = new TextField();

		public NameHeaderColumnFiltering() {
			this.getChildren().addAll(this.nameLabel, this.nameFieldFiltering);
			this.setPadding(new Insets(5));
			this.setAlignment(Pos.TOP_CENTER);
		}

		public TextField getNameFieldFiltering() {
			return this.nameFieldFiltering;
		}
	}

	private void installFiltering() {
		final FilteredList<PersonModel> filteredData = new FilteredList<>(this.memberList, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		this.nameHeaderColumnFiltering.getNameFieldFiltering().textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filteredData.setPredicate(person -> {
						// If filter text is empty, display all persons.
							if (newValue == null || newValue.isEmpty()) {
								return true;
							}

							final String lowerCaseFilter = newValue.toUpperCase();

							if (person.getName().toUpperCase().contains(lowerCaseFilter)) {
								return true; // Filter matches first
												// name.
							}
							return false; // Does not match.
						});
				});

		// 3. Wrap the FilteredList in a SortedList.
		final SortedList<PersonModel> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(this.membersTable.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		this.membersTable.setItems(sortedData);
	}

	public TableView<PersonModel> getMembersTable() {
		return this.membersTable;
	}

}
