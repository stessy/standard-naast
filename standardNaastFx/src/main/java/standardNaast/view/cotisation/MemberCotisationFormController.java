package standardNaast.view.cotisation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.observer.SeasonObserver;
import standardNaast.observer.SeasonSubjectImpl;
import standardNaast.service.CotisationsService;
import standardNaast.utils.AlertDialogUtils;
import standardNaast.view.member.overview.MemberCotisationsController;

public class MemberCotisationFormController implements SeasonObserver {

	private final List<String> validationErrors = new ArrayList<>();

	private final CotisationsService cotisationService = new CotisationsService();

	private final ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	private PersonModel personModel;

	private Stage dialogStage;

	@FXML
	private ComboBox<SeasonModel> season;

	@FXML
	private TextField amount;

	@FXML
	private DatePicker paymentDate;

	private MemberCotisationsController memberCotisationsController;

	@FXML
	private void initialize() {
		SeasonSubjectImpl.getInstance().registerObserver(this);
		final List<SeasonModel> seasons = SeasonSubjectImpl.getInstance().getSeasons();
		this.update(seasons);
		this.paymentDate.setValue(LocalDate.now());
	}

	@Override
	public void update(final List<SeasonModel> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.season.setItems(this.seasonsList);
	}

	@FXML
	private void onAdd() {
		if (this.isValid()) {
			try {
				final MemberCotisationsModel model = this.cotisationService.addMemberCotisation(this.personModel,
						this.season.getSelectionModel()
								.getSelectedItem(), this.paymentDate.getValue());
				AlertDialogUtils.displaySuccessALert("Cotisation ajoutée");
				this.memberCotisationsController.onCotisationAdded(model);
				this.dialogStage.close();
			} catch (final Exception e) {
				AlertDialogUtils.displayErrorAlert(this.dialogStage, e.getMessage());
			}
		}
		else {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		}

	}

	@FXML
	private void onCancel() {
		this.dialogStage.close();
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateSeason();
		return this.validationErrors.isEmpty();
	}

	private void validateSeason() {
		if (this.season.getSelectionModel().getSelectedItem() == null) {
			this.validationErrors.add("Saison non sélectionnée");
		}
	}

	@FXML
	private void onSelectedSeason() {
		this.amount.setText(String.valueOf(this.season.getSelectionModel().getSelectedItem().getCotisationAMount()));
	}

	public void setDialogStage(final Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setPerson(final PersonModel personModel) {
		this.personModel = personModel;
	}

	public void setParentController(final MemberCotisationsController memberCotisationsController) {
		this.memberCotisationsController = memberCotisationsController;
	}
}
