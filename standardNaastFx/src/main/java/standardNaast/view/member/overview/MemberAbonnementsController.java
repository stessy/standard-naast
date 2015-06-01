package standardNaast.view.member.overview;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import standardNaast.entities.Abonnement;
import standardNaast.model.MemberAbonnementModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.service.AbonnementService;
import standardNaast.types.AbonnementStatus;
import standardNaast.utils.AlertDialogUtils;
import standardNaast.view.abonnement.AbonnementFormController;

public class MemberAbonnementsController {

	private final AbonnementService abonnementService = new AbonnementService();

	private final ObservableList<MemberAbonnementModel> abonnementsList = FXCollections.observableArrayList();

	@FXML
	private TableView<MemberAbonnementModel> abonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, String> saison;

	@FXML
	private TableColumn<MemberAbonnementModel, Boolean> paye;

	@FXML
	private TableColumn<MemberAbonnementModel, String> bloc;

	@FXML
	private TableColumn<MemberAbonnementModel, String> rang;

	@FXML
	private TableColumn<MemberAbonnementModel, String> place;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> reduction;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> acompte;

	@FXML
	private TableColumn<MemberAbonnementModel, AbonnementStatus> status;

	@FXML
	private Button addButton;

	@FXML
	private Button updateButton;

	private PersonModel memberModel;

	private MemberAbonnementModel memberAbonnementModel;

	@FXML
	public void initialize() {
		this.abonnementsTable.setPlaceholder(new Label("Aucun abonnement pour le membre"));
		this.bindProperties();
		this.addMembersTableEvent();
	}

	private void addMembersTableEvent() {
		this.abonnementsTable.setRowFactory(tv -> {
			final TableRow<MemberAbonnementModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					this.onAbonnementSelected(row.getItem());
				}
			});
			return row;
		});
	}

	private void onAbonnementSelected(final MemberAbonnementModel item) {
		this.memberAbonnementModel = item;
		this.updateButton.setDisable(false);
	}

	public void onSelectedMember(final PersonModel personne) {
		this.memberModel = personne;
		final List<MemberAbonnementModel> memberAbonnements = this.abonnementService.getMemberAbonnements(personne);

		this.abonnementsList.clear();
		this.abonnementsList.addAll(memberAbonnements);
		this.abonnementsTable.setItems(this.abonnementsList);
		this.addButton.setDisable(false);
		this.updateButton.setDisable(true);
	}

	@FXML
	private void onAdd() {
		// Detect if a member can purchase an abonnement by checking if he paid
		// the cotisation and the Identity card validity date is OK
		final LocalDate passportValidity = this.memberModel.getPassportValidity();
		if (StringUtils.isEmpty(this.memberModel.getIdentityCardNumber())) {
			AlertDialogUtils.displayErrorAlert(null,
					"Ce membre ne peut pas commander d'abonnement!!!. Pas de numéro de carte d'identité!!!!!");
		} else if (passportValidity == null) {
			AlertDialogUtils
					.displayErrorAlert(null,
							"Ce membre ne peut pas commander d'abonnement!!!. Pas de date de validité de la carte d'identité!!!!!");
		}
		else if (LocalDate.now().compareTo(passportValidity) > 0) {
			AlertDialogUtils
					.displayErrorAlert(null,
							"Ce membre ne peut pas commander d'abonnement!!!. Date de validité de la carte d'identité dépassée!!!!!");
		}
		else {
			try {
				final FXMLLoader loader = new FXMLLoader();
				loader.setLocation(AbonnementFormController.class.getResource("AbonnementForm.fxml"));
				final GridPane pane = (GridPane) loader.load();
				final AbonnementFormController controller = (AbonnementFormController) loader.getController();
				controller.setMember(this.memberModel);
				controller.fillForm();
				controller.setParentController(this);
				final Stage benevolatDialog = new Stage();
				benevolatDialog.setTitle("Benevolat");
				benevolatDialog.initModality(Modality.APPLICATION_MODAL);
				final Scene scene = new Scene(pane);
				benevolatDialog.setScene(scene);
				controller.setStage(benevolatDialog);
				controller.addAcompteListener();
				benevolatDialog.show();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void onUpdate() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AbonnementFormController.class.getResource("AbonnementForm.fxml"));
			final GridPane pane = (GridPane) loader.load();
			final AbonnementFormController controller = (AbonnementFormController) loader.getController();
			controller.setMember(this.memberModel);
			controller.setAbonnement(this.memberAbonnementModel);
			controller.fillForm();
			controller.setNew(false);
			final Stage benevolatDialog = new Stage();
			benevolatDialog.setTitle("Benevolat");
			benevolatDialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			benevolatDialog.setScene(scene);
			controller.setStage(benevolatDialog);
			controller.addAcompteListener();
			benevolatDialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void bindProperties() {
		this.saison.setCellValueFactory(cellData -> cellData.getValue().saisonProperty().get().idProperty());
		this.paye.setCellValueFactory(cellData -> cellData.getValue().payeProperty());
		this.bloc.setCellValueFactory(cellData -> cellData.getValue().abonnementPriceProperty().get().blocProperty());
		this.rang.setCellValueFactory(cellData -> cellData.getValue().rangProperty());
		this.place.setCellValueFactory(cellData -> cellData.getValue().placeProperty());
		this.reduction.setCellValueFactory(cellData -> cellData.getValue().reductionProperty().asObject());
		this.acompte.setCellValueFactory(cellData -> cellData.getValue().acompteProperty().asObject());
		this.status.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
	}

	public static MemberAbonnementModel memberAbonnementOf(final Abonnement abonnement) {
		final MemberAbonnementModel model = new MemberAbonnementModel();
		model.setAbonnementId(abonnement.getId());
		model.setAcompte(abonnement.getAcompte().longValue());
		model.setPaye(abonnement.getPaye());
		model.setPlace(abonnement.getPlace());
		model.setRang(abonnement.getRang());
		model.setReduction(abonnement.getReduction());
		model.setSaison(SeasonModel.of(abonnement.getSaison()));
		model.setStatus(abonnement.getAbonnementStatus());
		return model;
	}

	public void onAddedAbonnement(final MemberAbonnementModel model) {
		this.abonnementsList.add(model);
		this.abonnementsTable.setItems(this.abonnementsList);
	}

}
