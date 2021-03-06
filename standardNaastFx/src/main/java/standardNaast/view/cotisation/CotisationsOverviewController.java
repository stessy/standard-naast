package standardNaast.view.cotisation;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import standardNaast.Main;
import standardNaast.model.CotisationViewModel;
import standardNaast.model.CotisationsOverviewModel;
import standardNaast.model.SeasonModel;
import standardNaast.observer.SeasonObserver;
import standardNaast.observer.SeasonSubjectImpl;
import standardNaast.service.CotisationsService;
import standardNaast.utils.TableauCotisations;

import java.time.LocalDate;
import java.util.List;

public class CotisationsOverviewController implements SeasonObserver {

    private CotisationsService cotisationService = new CotisationsService();

    private ObservableList<CotisationsOverviewModel> paidCotisationsMemberCardSentList = FXCollections
            .observableArrayList();

    private ObservableList<CotisationsOverviewModel> unpaiedCotisationsList = FXCollections.observableArrayList();

    private ObservableList<CotisationsOverviewModel> paidCotisationsMemberCardNotSentList = FXCollections
            .observableArrayList();

    private ObservableList<SeasonModel> seasonList = FXCollections.observableArrayList();

    // Tables
    @FXML
    private TableView<CotisationsOverviewModel> paidCotisationsMemberCardSentTable;

    @FXML
    private TableView<CotisationsOverviewModel> paidCotisationsMemberCardNotSentTable;

    @FXML
    private TableView<CotisationsOverviewModel> unpaidCotisationsTable;

    // Columns member card sent
    @FXML
    private TableColumn<CotisationsOverviewModel, LocalDate> paidCotisationsMemberCardSentDate;

    @FXML
    private TableColumn<CotisationsOverviewModel, String> paidCotisationsMemberCardSentName;

    @FXML
    private TableColumn<CotisationsOverviewModel, String> paidCotisationsMemberCardSentFirstName;

    @FXML
    private TableColumn<CotisationsOverviewModel, Long> paidCotisationsMemberCardSentMemberNumber;

    // Columns member card not sent
    @FXML
    private TableColumn<CotisationsOverviewModel, LocalDate> paidCotisationsMemberCardNotSentDate;

    @FXML
    private TableColumn<CotisationsOverviewModel, String> paidCotisationsMemberCardNotSentName;

    @FXML
    private TableColumn<CotisationsOverviewModel, String> paidCotisationsMemberCardNotSentFirstName;

    @FXML
    private TableColumn<CotisationsOverviewModel, Long> paidCotisationsMemberCardNotSentMemberNumber;

    // Columns unpaid
    @FXML
    private TableColumn<CotisationsOverviewModel, String> unpaidCotisationsName;

    @FXML
    private TableColumn<CotisationsOverviewModel, String> unpaidCotisationsFirstName;

    @FXML
    private TableColumn<CotisationsOverviewModel, Long> unpaidCotisationsMemberNumber;

    @FXML
    private ComboBox<SeasonModel> seasons;

    @FXML
    private TextField totalPaid;

    @FXML
    private TextField totalUnpaid;

    @FXML
    private TextField total;

    @FXML
    private Button printButton;

    @FXML
    private void initialize() {
        SeasonSubjectImpl.getInstance().registerObserver(this);
        final List<SeasonModel> seasons = SeasonSubjectImpl.getInstance().getSeasons();
        this.update(seasons);
    }

    @Override
    public void update(final List<SeasonModel> seasons) {
        this.seasonList.clear();
        this.seasonList.addAll(seasons);
        this.seasons.setItems(this.seasonList);
    }

    @FXML
    private void onSelectedSeason() {
        final CotisationViewModel paiedCotisationsPerSeason = this.cotisationService
                .getPaiedCotisationsPerSeason(this.seasons.getSelectionModel().getSelectedItem());
        this.buildPaidCotisationsMemberCardNotSent(paiedCotisationsPerSeason);
        this.bindPaidNotSentProperties();
        this.buildPaidCotisationsMemberCardSent(paiedCotisationsPerSeason);
        this.bindPaidSentProperties();
        this.buildUnpaid(paiedCotisationsPerSeason);
        this.bindUnpaid();
        this.totalPaid.setText(String.valueOf(paiedCotisationsPerSeason.getTotalPaidCotisations()));
        this.totalUnpaid.setText(String.valueOf(paiedCotisationsPerSeason.getTotalUnpaidCotisations()));
        this.total.setText(String.valueOf(paiedCotisationsPerSeason.getTotalAmountPaid()) + " €");
    }

    private void buildUnpaid(final CotisationViewModel paiedCotisationsPerSeason) {
        this.unpaiedCotisationsList.clear();
        this.unpaiedCotisationsList.addAll(paiedCotisationsPerSeason.getUnpaidCotisationsModel());
        this.unpaidCotisationsTable.setItems(this.unpaiedCotisationsList);
    }

    private void buildPaidCotisationsMemberCardSent(final CotisationViewModel paiedCotisationsPerSeason) {
        this.paidCotisationsMemberCardSentList.clear();
        this.paidCotisationsMemberCardSentList.addAll(paiedCotisationsPerSeason
                .getPaidCotisationsMemberCardSent());
        this.paidCotisationsMemberCardSentTable.setItems(this.paidCotisationsMemberCardSentList);
    }


    private void buildPaidCotisationsMemberCardNotSent(final CotisationViewModel paiedCotisationsPerSeason) {
        this.paidCotisationsMemberCardNotSentList.clear();
        this.paidCotisationsMemberCardNotSentList.addAll(paiedCotisationsPerSeason
                .getPaidCotisationsMemberCardNotSent());
        this.paidCotisationsMemberCardNotSentTable.setItems(this.paidCotisationsMemberCardNotSentList);
    }

    private void bindPaidSentProperties() {
        this.paidCotisationsMemberCardSentDate
                .setCellValueFactory(cellData -> cellData.getValue().paymentDateProperty());
        this.paidCotisationsMemberCardSentFirstName.setCellValueFactory(cellData -> cellData.getValue()
                .memberProperty().get().firstNameProperty());
        this.paidCotisationsMemberCardSentMemberNumber.setCellValueFactory(cellData -> cellData.getValue()
                .memberProperty().get().memberNumberProperty().asObject());
        this.paidCotisationsMemberCardSentName.setCellValueFactory(cellData -> cellData.getValue().memberProperty()
                .get().nameProperty());
    }

    private void bindPaidNotSentProperties() {
        this.paidCotisationsMemberCardNotSentDate.setCellValueFactory(cellData -> cellData.getValue()
                .paymentDateProperty());
        this.paidCotisationsMemberCardNotSentFirstName.setCellValueFactory(cellData -> cellData.getValue()
                .memberProperty().get().firstNameProperty());
        this.paidCotisationsMemberCardNotSentMemberNumber.setCellValueFactory(cellData -> cellData.getValue()
                .memberProperty().get().memberNumberProperty().asObject());
        this.paidCotisationsMemberCardNotSentName.setCellValueFactory(cellData -> cellData.getValue().memberProperty()
                .get().nameProperty());
    }

    private void bindUnpaid() {
        this.unpaidCotisationsFirstName.setCellValueFactory(cellData -> cellData.getValue().memberProperty().get()
                .firstNameProperty());
        this.unpaidCotisationsMemberNumber.setCellValueFactory(cellData -> cellData.getValue().memberProperty().get()
                .memberNumberProperty().asObject());
        this.unpaidCotisationsName.setCellValueFactory(cellData -> cellData.getValue().memberProperty().get()
                .nameProperty());
    }

    public void onPrintButton() {
        final TableauCotisations cotisations = new TableauCotisations();
        final String pdfPath = cotisations.creationTableau();
        final HostServices hostServices = Main.main.getHostServices();
        hostServices.showDocument(pdfPath);
    }

//	public void onMemberCardDelivered() {
//		final ObservableList<CotisationsOverviewModel> selectedItems = this.paidCotisationsMemberCardNotSentTable
//				.getSelectionModel()
//				.getSelectedItems();
//		if (selectedItems.isEmpty()) {
//			AlertDialogUtils.displayErrorAlert(null, "Aucun membre sélectionné");
//		} else {
//			this.cotisationService.setMemberCardAsSent(selectedItems,
//					this.seasons.getSelectionModel().getSelectedItem());
//			this.onSelectedSeason();
//		}
//	}
}
