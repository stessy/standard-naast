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
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import standardNaast.model.AbonnementPriceChampionshipModel;
import standardNaast.model.BenevolatModel;
import standardNaast.model.MemberAbonnementsModel;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;
import standardNaast.service.AbonnementService;
import standardNaast.service.BenevolatService;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;
import standardNaast.types.AbonnementStatus;
import standardNaast.types.CompetitionType;
import standardNaast.utils.AlertDialogUtils;

public class AbonnementFormController implements Observer {

	private final List<String> validationErrors = new ArrayList<>();

	private final AbonnementService abonnementService = new AbonnementService();

	private final BenevolatService benevolatService = new BenevolatService();

	private final SeasonService seasonService = new SeasonServiceImpl();

	private PersonModel memberModel;

	private MemberAbonnementsModel model;

	private Stage dialogStage;

	private boolean isNew = true;

	private final ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	private final ObservableList<CompetitionType> competitionTypes = FXCollections.observableArrayList();

	private final ObservableList<String> blocs = FXCollections.observableArrayList();

	@FXML
	private ComboBox<SeasonModel> saison;

	@FXML
	private ComboBox<CompetitionType> typeCompetition;

	@FXML
	private Label memberLabel;

	@FXML
	private ComboBox<String> bloc;

	@FXML
	private TextField rang;

	@FXML
	private TextField place;

	@FXML
	private TextField prixAbonnement;

	@FXML
	private TextField reduction;

	@FXML
	private TextField acompte;

	@FXML
	private TextField solde;

	@FXML
	private RadioButton paye;

	@FXML
	private RadioButton nonPaye;

	private AbonnementPriceChampionshipModel selectedAbonnementPrice;

	@FXML
	private void initialize() {
		final List<SeasonModel> seasons = SubjectImpl.getInstance().getSeasons();
		this.update(seasons);
		this.prepareCompetitionTypeBox(true);
	}

	private void prepareCompetitionTypeBox(final boolean disabled) {
		this.competitionTypes.clear();
		this.competitionTypes.addAll(CompetitionType.values());
		this.typeCompetition.setItems(this.competitionTypes);
		this.typeCompetition.setDisable(disabled);
	}

	@FXML
	private void onAddAbonnement(final ActionEvent event) {
		if (!this.isValid()) {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		} else {
			if (this.isNew) {
				final MemberAbonnementsModel model = new MemberAbonnementsModel();
				model.setStatus(AbonnementStatus.NEW);
				model.setAcompte(Long.valueOf(this.acompte.getText()));
				model.setPerson(this.memberModel);
				model.setPlace(this.place.getText());
				model.setAbonnementPrice(this.selectedAbonnementPrice);
				model.setRang(this.rang.getText());
				model.setReduction(Long.valueOf(this.reduction.getText()));
				model.setSaison(this.saison.getSelectionModel().getSelectedItem());
				this.abonnementService.addAbonnement(model);
			}
			else {
				this.model.setAcompte(Long.valueOf(this.acompte.getText()));
				this.model.setPlace(this.place.getText());
				this.model.setAbonnementPrice(this.selectedAbonnementPrice);
				this.model.setRang(this.rang.getText());
				this.model.setReduction(Long.valueOf(this.reduction.getText()));
				this.abonnementService.addAbonnement(this.model);
			}
			AlertDialogUtils.displaySuccessALert("Abonnement ajouté");
			this.dialogStage.close();
		}
	}

	@FXML
	private void onCancel(final ActionEvent event) {
		this.dialogStage.close();
	}

	@FXML
	private void onSelectedCompetitionType() {
		this.bloc.setDisable(true);
		this.blocs.clear();
		final List<String> blocList = this.abonnementService.getBlocList(this.saison.getSelectionModel()
				.getSelectedItem(), this.typeCompetition
				.getSelectionModel().getSelectedItem());
		if (blocList.isEmpty()) {
			AlertDialogUtils.displayErrorAlert(this.dialogStage, "Aucun bloc n'est disponible pour la saison ["
					+ this.saison.getSelectionModel().getSelectedItem().getId() + "] et le type de compétition ["
					+ this.typeCompetition.getSelectionModel().getSelectedItem().toString() + "]");
		}
		else {
			this.blocs.addAll(blocList);
			this.bloc.setItems(this.blocs);
			this.bloc.setDisable(false);
		}
	}

	@FXML
	private void onSelectedSeason() {
		this.prepareCompetitionTypeBox(false);
		this.bloc.setDisable(true);
		this.rang.setEditable(true);
		this.place.setEditable(true);
		if (this.isNew()) {
			// Recupérer la saison d'avant
			final int selectedIndex = this.saison.getSelectionModel().getSelectedIndex();
			final SeasonModel previousSeason = this.seasonsList.get(selectedIndex + 1);
			final MemberAbonnementsModel previousAbonnement = this.abonnementService.getPreviousAbonnement(
					previousSeason,
					this.memberModel.getPersonneId());
			if (previousAbonnement != null) {
				this.place.setText(String.valueOf(previousAbonnement.getPlace()));
				final AbonnementPriceChampionshipModel price = previousAbonnement.getAbonnementPrice();
				if (price != null) {
					this.bloc.getSelectionModel().select(price.getBloc());
				}
				this.rang.setText(String.valueOf(previousAbonnement.getRang()));
			}
			// Calculer la réduction
			final List<BenevolatModel> benevolatsForASeason = this.benevolatService.getBenevolatsForASeason(
					this.memberModel, previousSeason);
			long totalRefund = 0;
			final long benevolatRefund = benevolatsForASeason.stream().mapToLong(BenevolatModel::getMontant).sum();
			long travelsRefund = 0;
			final MemberSeasonTravels travelsPerSeason = this.seasonService.getTravelsPerSeason(previousSeason,
					this.memberModel.getPersonneId());
			final int away = travelsPerSeason.getAway();
			final int home = travelsPerSeason.getHome();
			if (away > 11 || home > 11) {
				travelsRefund = away + home;
			}
			totalRefund = benevolatRefund + travelsRefund;
			final StringBuilder builder = new StringBuilder();
			builder.append("Bénévolats: ");
			builder.append(benevolatRefund);
			builder.append("\n");
			builder.append("Déplacements : ");
			builder.append(travelsRefund);
			this.reduction.setText(String.valueOf(totalRefund));
			this.reduction.setTooltip(new Tooltip(builder.toString()));
		}
	}

	@FXML
	private void onSelectedBloc() {
		// Calculer prix abonnement
		final SeasonModel selectedSeason = this.saison.getSelectionModel().getSelectedItem();
		final String selectedBloc = this.bloc.getSelectionModel().getSelectedItem();
		this.selectedAbonnementPrice = this.abonnementService.getAbonnementPrice(
				selectedSeason, this.memberModel,
				selectedBloc, this.typeCompetition.getSelectionModel().getSelectedItem());
		this.prixAbonnement.setText(String.valueOf(this.selectedAbonnementPrice.getPrice()));
		// Calculer le solde
		this.calculateBalance();

	}

	private void calculateBalance() {
		final Long price = Long.valueOf(this.prixAbonnement.getText());
		final String reductionValue = this.reduction.getText();
		final Long reduction = Long.valueOf(StringUtils.isEmpty(reductionValue) ? "0" : reductionValue);
		final String text = this.acompte.getText();
		final Long acompte = Long.valueOf(StringUtils.isEmpty(text) ? "0" : text);
		this.solde.setText(String.valueOf(price - reduction - acompte));

	}

	public void fillForm() {
		this.memberLabel.setText(this.memberModel.getFirstName() + " " + this.memberModel.getName());
		this.acompte.setText("0");
		if (this.model != null) {
			this.saison.getSelectionModel().select(this.model.getSaison());
			this.bloc.getSelectionModel().select(this.model.getAbonnementPrice().getBloc());
			this.typeCompetition.getSelectionModel().select(this.model.getAbonnementPrice().getTypeCompetition());
			this.rang.setText(String.valueOf(this.model.getRang()));
			this.place.setText(String.valueOf(this.model.getPlace()));
			this.reduction.setText(String.valueOf(this.model.getReduction()));
			this.acompte.setText(String.valueOf(this.model.getAcompte()));
			final AbonnementStatus abonnementStatus = this.model.getStatus();
			this.saison.setDisable(abonnementStatus == AbonnementStatus.NEW ? false : true);
			this.bloc.setDisable(abonnementStatus == AbonnementStatus.NEW ? false : true);
			this.rang.setEditable(abonnementStatus == AbonnementStatus.NEW ? true : false);
			this.place.setEditable(abonnementStatus == AbonnementStatus.NEW ? true : false);
			this.prixAbonnement.setText(String.valueOf(this.model.getAbonnementPrice().getPrice()));
			this.calculateBalance();
		}

	}

	public void setNew(final boolean isNew) {
		this.isNew = isNew;
	}

	private boolean isNew() {
		return this.isNew;
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateBloc();
		this.validatePlace();
		this.validateRang();
		return this.validationErrors.isEmpty();
	}

	public void setStage(final Stage stage) {
		this.dialogStage = stage;
	}

	@Override
	public void update(final List<SeasonModel> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.saison.setItems(this.seasonsList);

	}

	private void validateBloc() {
		final String selectedItem = this.bloc.getSelectionModel().getSelectedItem();
		if (StringUtils.isEmpty(selectedItem)) {
			this.validationErrors.add("Bloc obligatoire");
		}
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

	public void setMember(final PersonModel memberModel) {
		this.memberModel = memberModel;
	}

	public void addAcompteListener() {
		this.acompte
				.textProperty()
				.addListener(
						(ov, t, t1) -> this.calculateBalance());
	}

	public void setAbonnement(final MemberAbonnementsModel abonnementModel) {
		this.model = abonnementModel;
	}
}
