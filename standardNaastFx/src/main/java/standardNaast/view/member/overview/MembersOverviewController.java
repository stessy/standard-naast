package standardNaast.view.member.overview;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TableRow;
import standardNaast.entities.Personne;
import standardNaast.model.PersonModel;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

public class MembersOverviewController {

	private final PersonneService personneService = new PersonneServiceImpl();

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
		final Personne person = this.personneService.getPerson(personModel.getPersonneId());
		this.memberFormController.fillForm(person);
		this.memberTravelsController.setPersonneId(personModel.getPersonneId());
		this.memberAbonnementsController.onSelectedMember(person);
		this.memberBenevolatsController.onMemberSelected(person);
		this.memberCotisationsController.onSelectedMember(person);
	}
}
