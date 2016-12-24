package standardNaast.view.member.overview;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.PersonModel;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;
import standardNaast.utils.AlertDialogUtils;

public class MemberFormController {

	PersonneService personneService = new PersonneServiceImpl();

	@FXML
	private TextField memberNumberLabel;

	@FXML
	private TextField firstNameLabel;

	@FXML
	private TextField nameLabel;

	@FXML
	private TextField addressLabel;

	@FXML
	private TextField postalCodeLabel;

	@FXML
	private TextField cityLabel;

	@FXML
	private TextField phoneNumberLabel;

	@FXML
	private TextField mobilePhoneLabel;

	@FXML
	private ToggleGroup studentLabel;

	@FXML
	private RadioButton studentYes;

	@FXML
	private RadioButton studentNo;

	@FXML
	private DatePicker birthDateLabel;

	@FXML
	private TextField identityCardNumberLabel;

	@FXML
	private DatePicker passwordValidityLabel;

	@FXML
	private TextField emailLabel;

	@FXML
	private Button addButton;

	@FXML
	private Button updateButton;

	@FXML
	private Button deleteButton;

	private PersonModel model;

	private final List<String> validationErrors = new ArrayList<>();

	private MembersOverviewController parentController;

	public void fillForm(final PersonModel model) {
		this.model = model;
		this.addButton.setDisable(false);
		this.updateButton.setDisable(false);
		this.deleteButton.setDisable(false);
		this.memberNumberLabel.setText(String.valueOf(model.getMemberNumber()));
		this.firstNameLabel.setText(model.getFirstName());
		this.nameLabel.setText(model.getName());
		this.addressLabel.setText(model.getAddress());
		this.postalCodeLabel.setText(model.getPostalCode());
		this.cityLabel.setText(model.getCity());
		this.phoneNumberLabel.setText(model.getPhone());
		this.mobilePhoneLabel.setText(model.getMobilePhone());
		this.studentYes.setSelected(model.getStudent());
		this.studentNo.setSelected(!model.getStudent());
		this.emailLabel.setText(model.getEmail());
		final LocalDate birthdate = model.getBirthdate();
		this.birthDateLabel.setValue(birthdate);
		this.identityCardNumberLabel.setText(model.getIdentityCardNumber());
		this.passwordValidityLabel.setValue(model.getPassportValidity());
	}

	@FXML
	private void onAddMember() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MemberFormController.class.getResource("NewMemberForm.fxml"));
			final TitledPane rootPane = (TitledPane) loader.load();
			final NewMemberFormController controller = (NewMemberFormController) loader.getController();
			final Stage memberFormDialog = new Stage();
			memberFormDialog.setWidth(750);
			memberFormDialog.setTitle("Membre");
			memberFormDialog.initModality(Modality.APPLICATION_MODAL);
			controller.setDialogStage(memberFormDialog);
			controller.setParentController(this.parentController);
			final Scene scene = new Scene(rootPane);
			memberFormDialog.setScene(scene);
			memberFormDialog.showAndWait();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onUpdate() {
		if (this.isValid()) {
			this.model.setFirstName(this.firstNameLabel.getText());
			this.model.setName(this.nameLabel.getText());
			this.model.setAddress(this.addressLabel.getText());
			this.model.setPostalCode(this.postalCodeLabel.getText());
			this.model.setCity(this.cityLabel.getText());
			this.model.setBirthdate(this.birthDateLabel.getValue());
			this.model.setEmail(this.emailLabel.getText());
			this.model.setIdentityCardNumber(this.identityCardNumberLabel.getText());
			this.model.setMobilePhone(this.mobilePhoneLabel.getText());
			this.model.setPassportValidity(this.passwordValidityLabel.getValue());
			this.model.setPhone(this.phoneNumberLabel.getText());
			this.model.setStudent(this.studentYes.isSelected() ? true : false);
			this.personneService.savePerson(this.model);
			AlertDialogUtils.displaySuccessALert("Les informations du membres ont bien été mises à jour");
		} else {
			AlertDialogUtils.displayInvalidAlert(null, this.validationErrors);
		}
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateFirstName();
		this.validateName();
		this.validateBirthDate();
		if (!this.validationErrors.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean validateFirstName() {
		boolean valid = true;
		if (StringUtils.isEmpty(this.firstNameLabel.getText())) {
			this.validationErrors.add("Prénom obligatoire");
			valid = false;
		}
		return valid;
	}

	private boolean validateName() {
		boolean valid = true;
		if (StringUtils.isEmpty(this.nameLabel.getText())) {
			this.validationErrors.add("Nom obligatoire");
			valid = false;
		}
		return valid;
	}

	private boolean validateBirthDate() {
		boolean valid = true;
		final LocalDate birthDate = this.birthDateLabel.getValue();
		if (birthDate == null) {
			this.validationErrors.add("Date de naissance obligatoire");
			valid = false;
		}
		return valid;
	}

	public void setParentController(final MembersOverviewController parentController) {
		this.parentController = parentController;
	}

	public void onDelete() {
		final ButtonType oui = new ButtonType("Oui", ButtonData.YES);
		final ButtonType non = new ButtonType("Non", ButtonData.NO);
		final ButtonType annuler = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
		final String memberFirstName = this.model.getFirstName();
		final String memberLastName = this.model.getName();
		final Alert alert = new Alert(AlertType.CONFIRMATION,
				"Souhaitez-vous effacer le membre " + memberFirstName + " " + memberLastName + " ?",
				oui, non, annuler);
		alert.showAndWait();
		if (alert.getResult() == oui) {
			final Long personneId = this.model.getPersonneId();
			this.personneService.deletePerson(personneId);
			this.parentController.onAddedMember(this.model);
			AlertDialogUtils.displaySuccessALert("Membre " + memberFirstName + " " + memberLastName + " effacé");
		}
	}
}
