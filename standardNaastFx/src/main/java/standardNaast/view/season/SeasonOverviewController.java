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
import standardNaast.entities.Season;
import standardNaast.model.SeasonModel;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;

public class SeasonOverviewController implements Observer {

	private final ObservableList<Season> seasonsList = FXCollections.observableArrayList();

	@FXML
	private Parent seasonInformation;

	@FXML
	private SeasonInformationController seasonInformationController;

	@FXML
	private ComboBox<Season> season;

	private ChangeListener<Season> seasonListener;

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
	public void update(final List<Season> seasons) {
		this.season.valueProperty().removeListener(this.seasonListener);
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.season.setItems(this.seasonsList);
		this.season.valueProperty().addListener(this.seasonListener);
		this.modifyButton.setDisable(true);
		this.seasonInformationController.reset();
	}

	private void onSelectedSeason(final Season selectedSeason) {
		final SeasonModel model = SeasonModel.of(selectedSeason);
		this.seasonInformationController.onSelectedSeason(model);
		this.modifyButton.setDisable(false);
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
			final Season selectedSeason = this.season.getSelectionModel().getSelectedItem();
			if (selectedSeason != null && !this.newSeason) {
				final SeasonModel selectedSeasonModel = SeasonModel.of(selectedSeason);
				controller.setModel(selectedSeasonModel);
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
