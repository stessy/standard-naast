package standardNaast.view.member.overview;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

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

	private PersonModel model;

	private Stage dialogStage;

	private final List<String> validationErrors = new ArrayList<>();

	public void fillForm(final PersonModel model) {
		this.model = model;
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
			loader.setLocation(MemberFormController.class.getResource("MemberForm.fxml"));
			final TitledPane rootPane = (TitledPane) loader.load();
			final AnchorPane anchorPane = (AnchorPane) rootPane.getContent();
			final ObservableList<Node> children = anchorPane.getChildren();
			final BorderPane borderPane = (BorderPane) children.get(0);
			final ButtonBar buttonBar = (ButtonBar) borderPane.getBottom();
			final ObservableList<Node> buttons = buttonBar.getButtons();
			buttons.stream().forEach(b -> b.setVisible(false));
			final Button addButton = new Button("Ajouter le membre");
			addButton.setOnAction(this::addMember);
			final Button cancelButton = new Button("Annuler");
			ButtonBar.setButtonData(addButton, ButtonData.YES);
			ButtonBar.setButtonData(cancelButton, ButtonData.CANCEL_CLOSE);
			buttonBar.getButtons().addAll(addButton, cancelButton);

			final MemberFormController controller = (MemberFormController) loader.getController();
			final Stage memberFormDialog = new Stage();
			memberFormDialog.setWidth(750);
			memberFormDialog.setTitle("Membre");
			memberFormDialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(rootPane);
			memberFormDialog.setScene(scene);
			controller.setDialogStage(memberFormDialog);
			memberFormDialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onUpdate() {
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
	}

	public void setDialogStage(final Stage stage) {
		this.dialogStage = stage;
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

	private void addMember(final ActionEvent event) {
		if (this.isValid()) {
			this.personneService.addPerson(this.model);
		} else {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		}
	}

	@FXML
	private void cancel(final Stage dialogStage) {
		dialogStage.close();
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
}
