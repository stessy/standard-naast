package standardNaast.view.season;

import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.SeasonModel;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;

public class SeasonOverviewController implements Observer {

	private final ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	@FXML
	private Parent seasonInformation;

	@FXML
	private Parent seasonTravels;

	@FXML
	private SeasonInformationController seasonInformationController;

	@FXML
	private SeasonTravelsTableController seasonTravelsController;

	@FXML
	private ComboBox<SeasonModel> season;

	private ChangeListener<SeasonModel> seasonListener;

	@FXML
	private Button addButton;

	@FXML
	private Button modifyButton;

	private boolean newSeason;

	@FXML
	private void initialize() {
		SubjectImpl.getInstance().registerObserver(this);
		this.modifyButton.setDisable(true);
		this.seasonListener = (ov, s1, s2) -> this.onSelectedSeason(ov.getValue());
		this.season.valueProperty().addListener(this.seasonListener);
		this.update(SubjectImpl.getInstance().getSeasons());
	}

	@Override
	public void update(final List<SeasonModel> seasons) {
		this.season.valueProperty().removeListener(this.seasonListener);
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.season.setItems(this.seasonsList);
		this.season.valueProperty().addListener(this.seasonListener);
		this.modifyButton.setDisable(true);
		this.seasonInformationController.reset();
	}

	private void onSelectedSeason(final SeasonModel selectedSeason) {
		this.seasonInformationController.onSelectedSeason(selectedSeason);
		this.modifyButton.setDisable(false);
		this.seasonTravelsController.fillTable(selectedSeason);
	}

	@FXML
	private void onAddAction() {
		this.newSeason = true;
		final Stage dialog = this.buildDialog();
		dialog.show();

	}

	@FXML
	private void onUpdateAction() {
		this.newSeason = false;
		final Stage dialog = this.buildDialog();
		dialog.show();
	}

	private Stage buildDialog() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(SeasonFormController.class.getResource("SeasonForm.fxml"));
			final GridPane pane = (GridPane) loader.load();
			final SeasonFormController controller = (SeasonFormController) loader.getController();
			final SeasonModel selectedSeason = this.season.getSelectionModel().getSelectedItem();
			if (selectedSeason != null && !this.newSeason) {
				controller.setModel(selectedSeason);
				controller.fillForm();
			}
			final Stage seasonDialog = new Stage();
			seasonDialog.setTitle("Saison");
			seasonDialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			seasonDialog.setScene(scene);
			controller.setDialogStage(seasonDialog);
			return seasonDialog;
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
