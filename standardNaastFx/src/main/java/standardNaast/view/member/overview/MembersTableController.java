package standardNaast.view.member.overview;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
import standardNaast.entities.Personne;
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

	private final FirstNameHeaderColumnFiltering firstNameHeaderColumnFiltering = new FirstNameHeaderColumnFiltering();

	private final NameHeaderColumnFiltering nameHeaderColumnFiltering = new NameHeaderColumnFiltering();

	@FXML
	private void initialize() {
		final List<Personne> allMembers = this.personneService.findAllPerson(false);
		this.buildModel(allMembers);
		this.bindProperties();
		this.installFiltering();

	}

	private void buildModel(final List<Personne> allMembers) {
		for (final Personne personne : allMembers) {
			final PersonModel personModel = new PersonModel();
			personModel.setMemberNumber(personne.getMemberNumber());
			personModel.setFirstName(personne.getFirstname());
			personModel.setName(personne.getName());
			personModel.setAddress(personne.getAddress());
			personModel.setPostalCode(personne.getPostalCode());
			personModel.setCity(personne.getCity());
			personModel.setPhone(personne.getPhone());
			personModel.setMobilePhone(personne.getMobilePhone());
			personModel.setStudent(personne.isStudent());
			personModel.setIdentityCardNumber(personne.getIdentityCardNumber());
			Date birthdate = personne.getBirthdate();
			if (birthdate == null) {
				birthdate = new Date(0);
			}
			personModel.setBirthdate(LocalDateTime.ofInstant(birthdate.toInstant(), ZoneId.systemDefault())
					.toLocalDate());
			personModel.setEmail(personne.getEmail());
			Date passportValidity = personne.getPassportValidity();
			if (passportValidity == null) {
				passportValidity = new Date(0);
			}
			personModel.setPassportValidity(LocalDateTime.ofInstant(passportValidity.toInstant(),
					ZoneId.systemDefault()).toLocalDate());
			personModel.setPersonneId(personne.getPersonneId());
			this.memberList.add(personModel);
		}

		this.membersTable.setItems(this.memberList);
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
		this.firstNameMemberColumn.setGraphic(this.firstNameHeaderColumnFiltering);
		this.memberNameColumn.setGraphic(this.nameHeaderColumnFiltering);
	}

	private class FirstNameHeaderColumnFiltering extends VBox {
		private final Label firstNameLabel = new Label("Prénom");

		private final TextField firstNameFieldFiltering = new TextField();

		public FirstNameHeaderColumnFiltering() {
			this.getChildren().addAll(this.firstNameLabel, this.firstNameFieldFiltering);
			this.setPadding(new Insets(5));
			this.setAlignment(Pos.TOP_CENTER);
		}

		public TextField getFirstNameFieldFiltering() {
			return this.firstNameFieldFiltering;
		}
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
		this.firstNameHeaderColumnFiltering.getFirstNameFieldFiltering().textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filteredData.setPredicate(person -> {
						// If filter text is empty, display all persons.
							if (newValue == null || newValue.isEmpty()) {
								return true;
							}

							final String lowerCaseFilter = newValue.toUpperCase();

							if (person.getFirstName().toUpperCase().contains(lowerCaseFilter)) {
								return true; // Filter matches first
												// name.
							}
							return false; // Does not match.
						});
				});
		this.nameHeaderColumnFiltering.getNameFieldFiltering().textProperty()
				.addListener((observable, oldValue, newValue) -> {
					filteredData.setPredicate(person -> {
						// If filter text is empty, display all persons.
							if (newValue == null || newValue.isEmpty()) {
								return true;
							}

							final String lowerCaseFilter = newValue.toUpperCase();

							if (person.getName().toUpperCase().contains(lowerCaseFilter)) {
								return true; // Filter matches last
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
