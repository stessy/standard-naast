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
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.service.CotisationsService;
import standardNaast.view.cotisation.MemberCotisationFormController;

public class MemberCotisationsController {

	private CotisationsService cotisationService = new CotisationsService();

	private final ObservableList<MemberCotisationsModel> memberCotisationsList = FXCollections.observableArrayList();

	@FXML
	private TableView<MemberCotisationsModel> memberCotisationsTable;

	@FXML
	private TableColumn<MemberCotisationsModel, SeasonModel> season;

	@FXML
	private TableColumn<MemberCotisationsModel, LocalDate> datePaiement;

	@FXML
	private TableColumn<MemberCotisationsModel, Long> montant;

	@FXML
	private Button addButton;

	private PersonModel personModel;

	@FXML
	public void initialize() {
		this.memberCotisationsTable.setPlaceholder(new Label("Aucune cotisation pour le membre"));
	}

	public void onSelectedMember(final PersonModel personModel) {
		this.personModel = personModel;
		final List<MemberCotisationsModel> personnesCotisations = this.cotisationService
				.getMemberCotisations(personModel);
		this.memberCotisationsList.clear();
		this.memberCotisationsList.addAll(personnesCotisations);
		this.memberCotisationsTable.setItems(this.memberCotisationsList);
		this.bindProperties();
		this.addButton.setDisable(false);
	}

	private void bindProperties() {
		this.season.setCellValueFactory(cellData ->
				cellData.getValue().seasonProperty());
		this.montant.setCellValueFactory(cellData -> cellData.getValue().montantProperty().asObject());
		this.datePaiement.setCellValueFactory(cellData -> cellData.getValue().datePaimentProperty());
	}

	@FXML
	private void onAdd() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MemberCotisationFormController.class.getResource("MemberCotisationForm.fxml"));
			final GridPane rootPane = (GridPane) loader.load();
			final MemberCotisationFormController controller = (MemberCotisationFormController) loader.getController();
			final Stage memberFormDialog = new Stage();
			memberFormDialog.setWidth(300);
			memberFormDialog.setTitle("Membre");
			memberFormDialog.initModality(Modality.APPLICATION_MODAL);
			controller.setDialogStage(memberFormDialog);
			controller.setPerson(this.personModel);
			final Scene scene = new Scene(rootPane);
			memberFormDialog.setScene(scene);
			memberFormDialog.showAndWait();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
