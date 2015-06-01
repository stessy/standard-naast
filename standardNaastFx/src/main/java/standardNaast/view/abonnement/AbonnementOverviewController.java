package standardNaast.view.abonnement;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import standardNaast.model.AbonnementsModel;
import standardNaast.model.MemberAbonnementModel;
import standardNaast.model.PurchasableAbonnements;
import standardNaast.model.SeasonModel;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;
import standardNaast.service.AbonnementService;
import standardNaast.utils.AlertDialogUtils;
import standardNaast.utils.PrintCommandesPlaces;

public class AbonnementOverviewController implements Observer {

	private AbonnementService service = new AbonnementService();

	private ObservableList<SeasonModel> observableSeasons = FXCollections.observableArrayList();

	private ObservableList<MemberAbonnementModel> newAbonnements = FXCollections.observableArrayList();

	private ObservableList<MemberAbonnementModel> unpaidAbonnements = FXCollections.observableArrayList();

	private ObservableList<MemberAbonnementModel> purchasedAbonnements = FXCollections.observableArrayList();

	private ObservableList<MemberAbonnementModel> receivedAbonnements = FXCollections.observableArrayList();

	private ObservableList<MemberAbonnementModel> distributedAbonnements = FXCollections.observableArrayList();

	@FXML
	private ComboBox<SeasonModel> season;

	@FXML
	private TableView<MemberAbonnementModel> newAbonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> newAbonnementsMemberNumber;

	@FXML
	private TableColumn<MemberAbonnementModel, String> newAbonnementsMemberFirstName;

	@FXML
	private TableColumn<MemberAbonnementModel, String> newAbonnementsMemberName;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> newAbonnementsAcompte;

	@FXML
	private TableView<MemberAbonnementModel> unpaidAbonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> unpaidAbonnementsMemberNumber;

	@FXML
	private TableColumn<MemberAbonnementModel, String> unpaidAbonnementsMemberFirstName;

	@FXML
	private TableColumn<MemberAbonnementModel, String> unpaidAbonnementsMemberName;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> unpaidAbonnementsAcompte;

	@FXML
	private TableView<MemberAbonnementModel> purchasedAbonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> purchasedAbonnementsMemberNumber;

	@FXML
	private TableColumn<MemberAbonnementModel, String> purchasedAbonnementsMemberFirstName;

	@FXML
	private TableColumn<MemberAbonnementModel, String> purchasedAbonnementsMemberName;

	@FXML
	private TableView<MemberAbonnementModel> receivedAbonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> receivedAbonnementsMemberNumber;

	@FXML
	private TableColumn<MemberAbonnementModel, String> receivedAbonnementsMemberFirstName;

	@FXML
	private TableColumn<MemberAbonnementModel, String> receivedAbonnementsMemberName;

	@FXML
	private TableView<MemberAbonnementModel> distributedAbonnementsTable;

	@FXML
	private TableColumn<MemberAbonnementModel, Long> distributedAbonnementsMemberNumber;

	@FXML
	private TableColumn<MemberAbonnementModel, String> distributedAbonnementsMemberFirstName;

	@FXML
	private TableColumn<MemberAbonnementModel, String> distributedAbonnementsMemberName;

	@FXML
	private Button printButton;

	@FXML
	private void initialize() {
		SubjectImpl.getInstance().registerObserver(this);
		final List<SeasonModel> seasons = SubjectImpl.getInstance().getSeasons();
		this.update(seasons);
		this.newAbonnementsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.unpaidAbonnementsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.purchasedAbonnementsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		this.receivedAbonnementsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@Override
	public void update(final List<SeasonModel> seasons) {
		this.observableSeasons.clear();
		this.observableSeasons.addAll(seasons);
		this.season.setItems(this.observableSeasons);
	}

	@FXML
	private void onSelectedSeason() {
		final SeasonModel selectedSeason = this.season.getSelectionModel().getSelectedItem();
		final AbonnementsModel abonnementsPerSeason = this.service.getAbonnementsPerSeason(selectedSeason);
		this.buildNewAbonnements(abonnementsPerSeason.getNewAbonnements());
		this.buildUnpaidAbonnements(abonnementsPerSeason.getUnpaidAbonnements());
		this.buildPurchasedAbonnements(abonnementsPerSeason.getPurchasedAbonnements());
		this.buildReceivedAbonnements(abonnementsPerSeason.getReceivedAbonnements());
		this.buildDistributedAbonnements(abonnementsPerSeason.getDistributedAbonnements());
		this.printButton.setDisable(false);
	}

	private void buildNewAbonnements(final List<MemberAbonnementModel> abonnements) {
		this.newAbonnements.clear();
		this.newAbonnements.addAll(abonnements);
		this.newAbonnementsTable.setItems(this.newAbonnements);
		this.bindNewAbonnementsProperties();
	}

	private void bindNewAbonnementsProperties() {
		this.newAbonnementsAcompte.setCellValueFactory(cellData -> cellData.getValue().soldeProperty().asObject());
		this.newAbonnementsMemberFirstName.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.firstNameProperty());
		this.newAbonnementsMemberName.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.nameProperty());
		this.newAbonnementsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.memberNumberProperty().asObject());
	}

	private void buildUnpaidAbonnements(final List<MemberAbonnementModel> abonnements) {
		this.unpaidAbonnements.clear();
		this.unpaidAbonnements.addAll(abonnements);
		this.unpaidAbonnementsTable.setItems(this.unpaidAbonnements);
		this.bindUnpaidAbonnementsProperties();
	}

	private void bindUnpaidAbonnementsProperties() {
		this.unpaidAbonnementsAcompte.setCellValueFactory(cellData -> cellData.getValue().soldeProperty().asObject());
		this.unpaidAbonnementsMemberFirstName.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.firstNameProperty());
		this.unpaidAbonnementsMemberName.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.nameProperty());
		this.unpaidAbonnementsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.memberNumberProperty().asObject());
	}

	private void buildPurchasedAbonnements(final List<MemberAbonnementModel> abonnements) {
		this.purchasedAbonnements.clear();
		this.purchasedAbonnements.addAll(abonnements);
		this.purchasedAbonnementsTable.setItems(this.purchasedAbonnements);
		this.bindPurchasedAbonnementsProperties();
	}

	private void bindPurchasedAbonnementsProperties() {
		this.purchasedAbonnementsMemberFirstName.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.firstNameProperty());
		this.purchasedAbonnementsMemberName.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.nameProperty());
		this.purchasedAbonnementsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.memberNumberProperty().asObject());
	}

	private void buildReceivedAbonnements(final List<MemberAbonnementModel> abonnements) {
		this.receivedAbonnements.clear();
		this.receivedAbonnements.addAll(abonnements);
		this.receivedAbonnementsTable.setItems(this.receivedAbonnements);
		this.bindReceivedAbonnementsProperties();
	}

	private void bindReceivedAbonnementsProperties() {
		this.receivedAbonnementsMemberFirstName.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.firstNameProperty());
		this.receivedAbonnementsMemberName.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.nameProperty());
		this.receivedAbonnementsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().personProperty().get()
				.memberNumberProperty().asObject());
	}

	private void buildDistributedAbonnements(final List<MemberAbonnementModel> abonnements) {
		this.distributedAbonnements.clear();
		this.distributedAbonnements.addAll(abonnements);
		this.distributedAbonnementsTable.setItems(this.distributedAbonnements);
		this.bindDistributedAbonnementsProperties();
	}

	private void bindDistributedAbonnementsProperties() {
		this.distributedAbonnementsMemberFirstName.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.firstNameProperty());
		this.distributedAbonnementsMemberName.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.nameProperty());
		this.distributedAbonnementsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().personProperty()
				.get()
				.memberNumberProperty().asObject());
	}

	@FXML
	private void setAsPurchased() {
		final ObservableList<MemberAbonnementModel> selectedItems = this.newAbonnementsTable.getSelectionModel()
				.getSelectedItems();
		if (selectedItems.isEmpty()) {
			AlertDialogUtils.displayErrorAlert(null, "Aucun nouvel abonnement sélectionné");
		}
		else {
			this.service.setAsPurchased(selectedItems);
			this.onSelectedSeason();
		}
	}

	@FXML
	private void setAsReceived() {
		final ObservableList<MemberAbonnementModel> selectedItems = this.purchasedAbonnementsTable.getSelectionModel()
				.getSelectedItems();
		if (selectedItems.isEmpty()) {
			AlertDialogUtils.displayErrorAlert(null, "Aucun abonnement commandé sélectionné");
		}
		else {
			this.service.setAsReceived(selectedItems);
			this.onSelectedSeason();
		}
	}

	@FXML
	private void setAsDistributed() {
		final ObservableList<MemberAbonnementModel> selectedItems = this.receivedAbonnementsTable.getSelectionModel()
				.getSelectedItems();
		if (selectedItems.isEmpty()) {
			AlertDialogUtils.displayErrorAlert(null, "Aucun abonnement reçu sélectionné");
		}
		else {
			this.service.setAsDistributed(selectedItems);
			this.onSelectedSeason();
		}
	}

	@FXML
	private void setAsPaid() {
		final ObservableList<MemberAbonnementModel> selectedItems = this.unpaidAbonnementsTable.getSelectionModel()
				.getSelectedItems();
		if (selectedItems.isEmpty()) {
			AlertDialogUtils.displayErrorAlert(null, "Aucun abonnement non payé sélectionné");
		}
		else {
			this.service.setAsPaid(selectedItems);
			this.onSelectedSeason();
		}
	}

	@FXML
	private void onPrint() {
		final SeasonModel selectedSeason = this.season
				.getSelectionModel().getSelectedItem();
		final List<PurchasableAbonnements> purchasableAbonnements = this.service.printAbonnements(selectedSeason);
		final PrintCommandesPlaces print = new PrintCommandesPlaces();
		final String path = print.printCommandeAbonnements(selectedSeason, purchasableAbonnements);
		try {
			Runtime.getRuntime().exec(
					new String[] { "rundll32", "url.dll,FileProtocolHandler",
							path });
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
