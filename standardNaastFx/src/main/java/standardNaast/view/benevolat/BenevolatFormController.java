package standardNaast.view.benevolat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

import standardNaast.entities.Personne;
import standardNaast.model.BenevolatModel;
import standardNaast.service.BenevolatService;
import standardNaast.utils.AlertDialogUtils;
import standardNaast.view.member.overview.MemberBenevolatsController;

public class BenevolatFormController {

	BenevolatService benevolatService = new BenevolatService();

	private final List<String> errors = new ArrayList<>();

	@FXML
	private DatePicker dateBenevolat;

	@FXML
	private TextField montantBenevolat;

	@FXML
	private TextArea description;

	private Stage dialogStage;

	private Personne person;

	private MemberBenevolatsController memberBenevolatsController;

	private BenevolatModel benevolatModel;

	@FXML
	private void initialize() {
		this.formatDatePicker();
	}

	@FXML
	public void onAddBenevolat() {
		if (!this.isValid()) {
			this.displayInvalidAlert();
		} else {
			final BenevolatModel model = this.buildBenevolat();
			this.benevolatService.addBenevolat(model, this.person);
			this.memberBenevolatsController.onBenevolatAdded(model);
			this.dialogStage.close();
		}

	}

	@FXML
	public void onModifyBenevolat() {
		if (!this.isValid()) {
			this.displayInvalidAlert();
		} else {
			this.benevolatModel.setDate(this.dateBenevolat.getValue());
			this.benevolatModel.setMontant(Long.valueOf(this.montantBenevolat.getText()));
			this.benevolatModel.setDescription(this.description.getText());
			this.benevolatService.saveBenevolat(this.benevolatModel);
			this.dialogStage.close();
		}

	}

	private BenevolatModel buildBenevolat() {
		final BenevolatModel model = new BenevolatModel();
		model.setDate(this.dateBenevolat.getValue());
		model.setDescription(this.description.getText());
		model.setMontant(Long.valueOf(this.montantBenevolat.getText()));
		return model;
	}

	private void displayInvalidAlert() {
		AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.errors);
	}

	public void fillForm() {
		if (this.benevolatModel != null) {
			this.dateBenevolat.setValue(this.benevolatModel.getDate());
			this.description.setText(this.benevolatModel.getDescription());
			this.montantBenevolat.setText(String.valueOf(this.benevolatModel.getMontant()));
		}
	}

	// Validation start

	private boolean isValid() {
		this.errors.clear();
		this.validateDate();
		this.validateDescription();
		this.validateMontant();
		return this.errors.isEmpty();
	}

	private boolean validateDate() {
		boolean valid = true;
		final LocalDate value = this.dateBenevolat.getValue();
		if (value == null) {
			this.errors.add("Le date du bénévolat est obligatoire");
			valid = false;
		}
		return valid;

	}

	private boolean validateMontant() {
		boolean valid = true;
		final String montant = this.montantBenevolat.getText();
		if (StringUtils.isEmpty(montant)) {
			this.errors.add("Le montant du bénévolat est obligatoire");
			valid = false;
		} else if (!StringUtils.isNumeric(montant)) {
			this.errors.add("Le montant du bénévolat n'est pas au bon format");
			valid = false;
		}
		return valid;
	}

	private boolean validateDescription() {
		boolean valid = true;
		final String description = this.description.getText();
		if (StringUtils.isEmpty(description)) {
			this.errors.add("La description est obligatoire");
			valid = false;
		}
		return valid;
	}

	// Validation end

	private void formatDatePicker() {
		final String pattern = "dd-MM-yyyy";

		this.dateBenevolat.setPromptText(pattern.toLowerCase());

		this.dateBenevolat.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			@Override
			public String toString(final LocalDate date) {
				if (date != null) {
					return this.dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(final String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, this.dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

	public void setDialogStage(final Stage stage) {
		this.dialogStage = stage;
	}

	public void setPerson(final Personne person) {
		this.person = person;
	}

	public void setMemberBenevolatsController(final MemberBenevolatsController memberBenevolatsController) {
		this.memberBenevolatsController = memberBenevolatsController;
	}

	public void setBenevolatModel(final BenevolatModel benevolatModel) {
		this.benevolatModel = benevolatModel;
	}

}
