package standardNaast.view.abonnement;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import standardNaast.entities.Bloc;
import standardNaast.entities.Season;
import standardNaast.model.AbonnementModel;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;
import standardNaast.service.AbonnementService;
import standardNaast.types.AbonnementStatus;
import standardNaast.utils.AlertDialogUtils;

public class AbonnementFormController implements Observer {

	private final List<String> validationErrors = new ArrayList<>();

	private final AbonnementService abonnementService = new AbonnementService();

	private AbonnementModel model;

	private Stage dialogStage;

	private boolean isNew;

	private final ObservableList<Season> seasonsList = FXCollections.observableArrayList();

	@FXML
	private ComboBox<Season> saison;

	@FXML
	private Label memberLabel;

	@FXML
	private ComboBox<Bloc> bloc;

	@FXML
	private TextField rang;

	@FXML
	private TextField place;

	@FXML
	private Label prixAbonnement;

	@FXML
	private Label reduction;

	@FXML
	private TextField acompte;

	@FXML
	private Label solde;

	@FXML
	private RadioButton paye;

	@FXML
	private RadioButton nonPaye;

	@FXML
	private void initialize() {
		final List<Season> seasons = SubjectImpl.getInstance().getSeasons();
		this.update(seasons);
	}

	@FXML
	private void onAddAbonnement(final ActionEvent event) {
		if (!this.isValid()) {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		} else {

		}
	}

	@FXML
	private void onCancel(final ActionEvent event) {
		this.dialogStage.close();
	}

	@FXML
	private void onSelectedSeason() {
		this.bloc.setDisable(false);
		this.rang.setEditable(true);
		this.place.setEditable(true);
		if (this.isNew()) {
			// Recupérer la saison d'avant
			final int selectedIndex = this.saison.getSelectionModel().getSelectedIndex();
			final Season previousSeason = this.seasonsList.get(selectedIndex + 1);
			final AbonnementModel previousAbonnement = this.abonnementService.getPreviousAbonnement(previousSeason,
					this.model.getMemberId());
			this.place.setText(String.valueOf(previousAbonnement.getPlace()));
			this.bloc.getSelectionModel().select(previousAbonnement.getBloc());
			this.rang.setText(String.valueOf(previousAbonnement.getRang()));
			// Calculer la réduction
		}
	}

	@FXML
	private void onSelectedBloc() {
		// Calculer prix abonnement
		final Season selectedSeason = this.saison.getSelectionModel().getSelectedItem();
		// Calculer le solde
	}

	public void fillForm() {
		if (this.model != null) {
			this.memberLabel.setText(this.model.getMemberFirstName() + " " + this.model.getMemberName());
			this.saison.getSelectionModel().select(this.model.getSaison());
			this.bloc.getSelectionModel().select(this.model.getBloc());
			this.rang.setText(String.valueOf(this.model.getRang()));
			this.place.setText(String.valueOf(this.model.getPlace()));
			this.reduction.setText(String.valueOf(this.model.getReduction()));
			this.acompte.setText(String.valueOf(this.model.getAcompte()));
			final AbonnementStatus abonnementStatus = this.model.getAbonnementStatus();
			this.saison.setDisable(true);
			this.bloc.setDisable(abonnementStatus == AbonnementStatus.NEW ? false : true);
			this.rang.setEditable(abonnementStatus == AbonnementStatus.NEW ? true : false);
			this.place.setEditable(abonnementStatus == AbonnementStatus.NEW ? true : false);
		}
	}

	private void setNew(final boolean isNew) {
		this.isNew = isNew;
	}

	private boolean isNew() {
		return this.isNew;
	}

	private boolean isValid() {
		this.validationErrors.clear();
		return this.validationErrors.isEmpty();
	}

	public void setStage(final Stage stage) {
		this.dialogStage = stage;
	}

	@Override
	public void update(final List<Season> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.saison.setItems(this.seasonsList);

	}

	private void validateBloc() {
		final int selectedIndex = this.bloc.getSelectionModel().getSelectedIndex();
	}

	private void validateRang() {
		final String rangValue = this.rang.getText();
		if (StringUtils.isEmpty(rangValue)) {
			this.validationErrors.add("Rang obligatoire");
		} else if (!StringUtils.isNumeric(rangValue)) {
			this.validationErrors.add("Rang invalide. Sont uniquement acceptées les valeurs numériques");
		}
	}

	private void validatePlace() {
		final String value = this.place.getText();
		if (StringUtils.isEmpty(value)) {
			this.validationErrors.add("Place obligatoire");
		} else if (!StringUtils.isNumeric(value)) {
			this.validationErrors.add("Place invalide. Sont uniquement acceptées les valeurs numériques");
		}
	}
}
