package standardNaast.view.member.overview;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.MemberAbonnementModel;
import standardNaast.model.PersonModel;
import standardNaast.service.AbonnementService;
import standardNaast.types.AbonnementStatus;
import standardNaast.view.abonnement.AbonnementFormController;

import java.io.IOException;
import java.util.List;

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
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AbonnementFormController.class.getResource("AbonnementForm.fxml"));
            final GridPane pane = loader.load();
            final AbonnementFormController controller = loader.getController();
            controller.setMember(this.memberModel);
            controller.fillForm();
            controller.setParentController(this);
            final Stage abonnementDialog = new Stage();
            abonnementDialog.setTitle("Abonnement");
            abonnementDialog.initModality(Modality.APPLICATION_MODAL);
            final Scene scene = new Scene(pane);
            abonnementDialog.setScene(scene);
            controller.setStage(abonnementDialog);
            controller.addAcompteListener();
            abonnementDialog.show();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onUpdate() {
        try {
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AbonnementFormController.class.getResource("AbonnementForm.fxml"));
            final GridPane pane = loader.load();
            final AbonnementFormController controller = loader.getController();
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

    public void onAddedAbonnement(final MemberAbonnementModel model) {
        this.abonnementsList.add(model);
        this.abonnementsTable.setItems(this.abonnementsList);
    }

}
