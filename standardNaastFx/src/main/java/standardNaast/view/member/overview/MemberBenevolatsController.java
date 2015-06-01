package standardNaast.view.member.overview;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.BenevolatModel;
import standardNaast.model.PersonModel;
import standardNaast.service.BenevolatService;
import standardNaast.view.benevolat.BenevolatFormController;

public class MemberBenevolatsController {

	BenevolatService benevolatService = new BenevolatService();

	private final ObservableList<BenevolatModel> benevolatList = FXCollections.observableArrayList();

	@FXML
	private TableView<BenevolatModel> memberBenevolatsTable;

	@FXML
	private TableColumn<BenevolatModel, LocalDate> date;

	@FXML
	private TableColumn<BenevolatModel, Long> montant;

	@FXML
	private TableColumn<BenevolatModel, String> description;

	@FXML
	private Button addButton;

	@FXML
	private Button modifyButton;

	@FXML
	private Button deleteButton;

	private BenevolatModel selectedBenevolat;

	private PersonModel personModel;

	@FXML
	public void initialize() {
		this.memberBenevolatsTable.setPlaceholder(new Label("Aucun bénévolat pour le membre"));
		this.memberBenevolatsTable.setRowFactory(tv -> {
			final TableRow<BenevolatModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					this.selectedBenevolat = row.getItem();
					this.modifyButton.setDisable(false);
					this.deleteButton.setDisable(false);
				}
			});
			return row;
		});
	}

	public void onMemberSelected(final PersonModel personModel) {
		this.personModel = personModel;
		final List<BenevolatModel> benevolats = this.benevolatService.getBenevolats(personModel);
		this.benevolatList.clear();
		this.benevolatList.addAll(benevolats);
		this.memberBenevolatsTable.setItems(this.benevolatList);
		this.bindProperties();
		this.addButton.setDisable(false);
		this.deleteButton.setDisable(false);
		this.modifyButton.setDisable(false);
	}

	public void bindProperties() {
		this.date.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		this.montant.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());
		this.description.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
	}

	@FXML
	public void onAddBenevolat() {
		this.displayBenevolatDialog(true);
	}

	private void displayBenevolatDialog(final boolean isNew) {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(BenevolatFormController.class.getResource("BenevolatForm.fxml"));
			final BorderPane pane = (BorderPane) loader.load();
			final BenevolatFormController controller = (BenevolatFormController) loader.getController();
			controller.setParentController(this);
			controller.setPerson(this.personModel);
			controller.setBenevolatModel(this.selectedBenevolat);
			controller.setParentController(this);
			controller.setNew(isNew);
			controller.fillForm();
			final Stage benevolatDialog = new Stage();
			benevolatDialog.setTitle("Benevolat");
			benevolatDialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			benevolatDialog.setScene(scene);
			controller.setDialogStage(benevolatDialog);
			benevolatDialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void onBenevolatAdded(final BenevolatModel model) {
		this.benevolatList.add(model);
		this.memberBenevolatsTable.setItems(this.benevolatList);
		this.bindProperties();
	}

	@FXML
	public void onDeleteBenevolat() {
		final Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Effacer bénévolat");
		alert.setHeaderText("Confirmation");
		alert.setContentText("Êtes-vous sur de vouloir effacer le bénévolat?");
		final Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			this.benevolatService.deleteBenevolat(this.selectedBenevolat);
			this.benevolatList.remove(this.selectedBenevolat);
			this.memberBenevolatsTable.setItems(this.benevolatList);
		} else {
			alert.close();
		}
	}

	@FXML
	public void onModifyBenevolat() {
		this.displayBenevolatDialog(false);
	}

}
