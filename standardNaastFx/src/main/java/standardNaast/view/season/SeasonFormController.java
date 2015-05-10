package standardNaast.view.season;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import standardNaast.model.SeasonModel;
import standardNaast.observer.SubjectImpl;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;
import standardNaast.utils.AlertDialogUtils;

public class SeasonFormController {

	private final SeasonService seasonService = new SeasonServiceImpl();

	private final List<String> validationErrors = new ArrayList<>();

	@FXML
	private DatePicker startDate;

	@FXML
	private DatePicker endDate;

	@FXML
	private DatePicker firstMatchDate;

	@FXML
	private RadioButton european;

	@FXML
	private RadioButton notEuropean;

	@FXML
	private TextField cotisationAmount;

	@FXML
	private ToggleGroup europeanToggleGroup;

	private boolean isNew = true;

	private SeasonModel model;

	private Stage dialogStage;

	@FXML
	private Button actionButton;

	@FXML
	private void onAddSeason() {
		if (this.isValid()) {
			if (this.isNew) {
				this.seasonService.addSeason(this.buildSeasonModel());
			}
			else {
				this.seasonService.updateSeason(this.updateSeasonModel());
			}
			SubjectImpl.getInstance().notifyObservers();
			this.dialogStage.close();
		} else {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		}
	}

	@FXML
	private void onCancel() {
		this.dialogStage.close();
	}

	private SeasonModel buildSeasonModel() {
		final SeasonModel model = new SeasonModel();
		model.setCotisationAMount(Long.valueOf(this.cotisationAmount.getText()));
		model.setEndDate(this.endDate.getValue());
		model.setStartDate(this.startDate.getValue());
		model.setFirstMatchDate(this.firstMatchDate.getValue());
		model.setEuropean(this.european.isSelected() ? true : false);
		return model;
	}

	private SeasonModel updateSeasonModel() {
		final SeasonModel updateModel = this.buildSeasonModel();
		updateModel.setId(this.model.getId());
		return updateModel;
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateStartDate();
		this.validateEndDate();
		this.validateFirstMatchDate();
		this.validateCotisationAmount();
		return this.validationErrors.isEmpty();
	}

	private void validateStartDate() {
		final LocalDate startDateValidation = this.startDate.getValue();
		final LocalDate endDateValidation = this.endDate.getValue();
		if (startDateValidation == null) {
			this.validationErrors.add("Date de début de saison obligatoire");
		} else if (endDateValidation != null && startDateValidation.isAfter(endDateValidation)) {
			this.validationErrors
					.add("La date de début de saison ne peux pas être supérieure à la date de fin de saison");
		}
	}

	private void validateEndDate() {
		final LocalDate startDateValidation = this.startDate.getValue();
		final LocalDate endDateValidation = this.endDate.getValue();
		if (endDateValidation == null) {
			this.validationErrors.add("Date de fin de saison obligatoire");
		} else if (startDateValidation != null && startDateValidation.isAfter(endDateValidation)) {
			this.validationErrors
					.add("La date de fin de saison ne peux pas être inférieure à la date de début de saison");
		}
	}

	private void validateFirstMatchDate() {
		final LocalDate startDateValidation = this.startDate.getValue();
		final LocalDate endDateValidation = this.endDate.getValue();
		final LocalDate firstMatchDateValidation = this.firstMatchDate.getValue();
		if (firstMatchDateValidation == null) {
			this.validationErrors.add("Date du premier match obligatoire");
		} else if (startDateValidation != null
				&& endDateValidation != null
				&& (firstMatchDateValidation.isAfter(endDateValidation) || firstMatchDateValidation
						.isBefore(startDateValidation))) {
			this.validationErrors
					.add("La date de premier match du championnat doit se trouver entre la date de début de saison et la date de fin de saison");
		}
	}

	private void validateCotisationAmount() {
		final String cotisation = this.cotisationAmount.getText();
		if (StringUtils.isEmpty(cotisation)) {
			this.validationErrors.add("Le montant de la cotisation est obligatoire");
		}
		else if (!StringUtils.isNumeric(cotisation)) {
			this.validationErrors
					.add("Le montant de la cotisation est invalide. Le montant de la cotisation doit être numérique.");

		}
	}

	public void fillForm() {
		if (this.model != null) {
			this.cotisationAmount.setText(String.valueOf(this.model.getCotisationAMount()));
			this.endDate.setValue(this.model.getEndDate());
			this.european.setSelected(this.model.getEuropean());
			this.notEuropean.setSelected(!this.model.getEuropean());
			this.firstMatchDate.setValue(this.model.getFirstMatchDate());
			this.startDate.setValue(this.model.getStartDate());
		}
	}

	public void setNew(final boolean isNew) {
		this.isNew = isNew;
		this.actionButton.setText("Modifier");

	}

	public void setModel(final SeasonModel model) {
		this.model = model;
		this.setNew(false);
	}

	public void setDialogStage(final Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
