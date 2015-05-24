package standardNaast.view.member.overview;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import standardNaast.entities.Abonnement;
import standardNaast.model.MemberAbonnementsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.service.AbonnementService;
import standardNaast.types.AbonnementStatus;

public class MemberAbonnementsController {

	private final AbonnementService abonnementService = new AbonnementService();

	private final ObservableList<MemberAbonnementsModel> abonnementsList = FXCollections.observableArrayList();

	@FXML
	private TableView<MemberAbonnementsModel> abonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementsModel, String> saison;

	@FXML
	private TableColumn<MemberAbonnementsModel, Boolean> paye;

	@FXML
	private TableColumn<MemberAbonnementsModel, String> bloc;

	@FXML
	private TableColumn<MemberAbonnementsModel, String> rang;

	@FXML
	private TableColumn<MemberAbonnementsModel, String> place;

	@FXML
	private TableColumn<MemberAbonnementsModel, Long> reduction;

	@FXML
	private TableColumn<MemberAbonnementsModel, Long> acompte;

	@FXML
	private TableColumn<MemberAbonnementsModel, AbonnementStatus> status;

	@FXML
	private Button addButton;

	@FXML
	private Button updateButton;

	@FXML
	public void initialize() {
		this.abonnementsTable.setPlaceholder(new Label("Aucun abonnement pour le membre"));
	}

	public void onSelectedMember(final PersonModel personne) {
		final List<MemberAbonnementsModel> memberAbonnements = this.abonnementService.getMemberAbonnements(personne);

		this.abonnementsList.clear();
		this.abonnementsList.addAll(memberAbonnements);
		this.abonnementsTable.setItems(this.abonnementsList);
		this.bindProperties();
		this.addButton.setDefaultButton(false);
		// this.updateButton.setDisable(false);
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

	public static MemberAbonnementsModel memberAbonnementOf(final Abonnement abonnement) {
		final MemberAbonnementsModel model = new MemberAbonnementsModel();
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

}
