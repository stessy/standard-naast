package standardNaast.view.member.overview;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import standardNaast.constants.DateFormat;
import standardNaast.model.PersonModel;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;
import standardNaast.utils.AlertDialogUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewMemberFormController {

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
    private CheckBox redCard;

    @FXML
    private TextField emailLabel;

    private Stage dialogStage;

    private final List<String> validationErrors = new ArrayList<>();

    private MembersOverviewController parentController;

    @FXML
    public void initialize() {
        this.birthDateLabel.setPromptText(DateFormat.DDSMMSYYYY);
    }

    public void setDialogStage(final Stage stage) {
        this.dialogStage = stage;
    }

    private boolean isValid() {
        this.validationErrors.clear();
        this.validateFirstName();
        this.validateName();
        this.validateBirthDate();
        return this.validationErrors.isEmpty();
    }

    @FXML
    private void onAddMember() {
        if (this.isValid()) {
            final PersonModel model = new PersonModel();
            model.setAddress(this.addressLabel.getText());
            model.setBirthdate(this.birthDateLabel.getValue());
            model.setCity(this.cityLabel.getText());
            model.setEmail(this.emailLabel.getText());
            model.setFirstName(this.firstNameLabel.getText());
            model.setMobilePhone(this.mobilePhoneLabel.getText());
            model.setName(this.nameLabel.getText());
            model.setPhone(this.phoneNumberLabel.getText());
            model.setPostalCode(this.postalCodeLabel.getText());
            model.setStudent(this.studentYes.isSelected());
            model.setRedCard(this.redCard.isSelected());
            final PersonModel addPerson = this.personneService.addPerson(model);
            this.parentController.onAddedMember(addPerson);
            this.dialogStage.close();
        } else {
            AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
        }
    }

    @FXML
    private void onCancel() {
        this.dialogStage.close();
    }

    private void validateFirstName() {
        if (StringUtils.isEmpty(this.firstNameLabel.getText())) {
            this.validationErrors.add("Prénom obligatoire");
        }
    }

    private void validateName() {
        if (StringUtils.isEmpty(this.nameLabel.getText())) {
            this.validationErrors.add("Nom obligatoire");
        }
    }

    private void validateBirthDate() {
        final LocalDate birthDate = this.birthDateLabel.getValue();
        if (birthDate == null) {
            this.validationErrors.add("Date de naissance obligatoire");
        }
    }

    public void setParentController(final MembersOverviewController parentController) {
        this.parentController = parentController;
    }
}
