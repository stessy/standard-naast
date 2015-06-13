package standardNaast.view.member.overview;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableRow;
import standardNaast.model.PersonModel;

public class MembersOverviewController {

	@FXML
	private Parent memberTravels;

	@FXML
	private Parent membersTable;

	@FXML
	private Parent memberForm;

	@FXML
	private Parent memberAbonnements;

	@FXML
	private Parent memberBenevolats;

	@FXML
	private Parent memberCotisations;

	@FXML
	private MemberFormController memberFormController;

	@FXML
	private MemberTravelsController memberTravelsController;

	@FXML
	private MembersTableController membersTableController;

	@FXML
	private MemberAbonnementsController memberAbonnementsController;

	@FXML
	private MemberBenevolatsController memberBenevolatsController;

	@FXML
	private MemberCotisationsController memberCotisationsController;

	@FXML
	private void initialize() {
		this.addMembersTableEvent();
		this.propagateController();
	}

	private void addMembersTableEvent() {
		this.membersTableController.getMembersTable().setRowFactory(tv -> {
			final TableRow<PersonModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					this.onMemberTableRowSelected(row.getItem());
				}
			});
			return row;
		});
	}

	private void onMemberTableRowSelected(final PersonModel personModel) {
		this.memberFormController.fillForm(personModel);
		this.memberTravelsController.setPersonneId(personModel.getPersonneId());
		this.memberAbonnementsController.onSelectedMember(personModel);
		this.memberBenevolatsController.onMemberSelected(personModel);
		this.memberCotisationsController.onSelectedMember(personModel);
	}

	public void onAddedMember(final PersonModel addPerson) {
		this.membersTableController.buildModel();
	}

	public MembersTableController getMembersTableController() {
		return this.membersTableController;
	}

	private void propagateController() {
		this.memberFormController.setParentController(this);
		this.memberCotisationsController.setParentController(this);
	}
}
