package standardNaast.view.season;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import standardNaast.entities.Season;
import standardNaast.model.SeasonModel;
import standardNaast.model.TravelModel;
import standardNaast.service.TravelService;

public class SeasonTravelsTableController {

	private final ObservableList<TravelModel> travels = FXCollections.observableArrayList();

	private final TravelService travelService = new TravelService();

	private TravelModel selectedTravel;

	@FXML
	private TableView<TravelModel> travelsTable;

	@FXML
	private TableColumn<TravelModel, Long> amount;

	@FXML
	private TableColumn<TravelModel, String> category;

	@FXML
	private TableColumn<TravelModel, String> place;

	@FXML
	private TableColumn<TravelModel, Boolean> member;

	@FXML
	private Button modify;

	@FXML
	private Button add;

	@FXML
	private Button delete;

	@FXML
	private void onSelectedRow() {
		this.selectedTravel = this.travelsTable.getSelectionModel().getSelectedItem();
		this.modify.setDisable(false);
		this.delete.setDisable(false);
	}

	@FXML
	private void onModify() {

	}

	@FXML
	private void onAdd() {

	}

	@FXML
	private void onDelete() {

	}

	public void fillTable(final Season selectedSeason) {
		this.travels.clear();
		final SeasonModel model = SeasonModel.of(selectedSeason);
		final List<TravelModel> travelsPerSeason = this.travelService.getTravelsPerSeason(model);
		this.travels.addAll(travelsPerSeason);
		this.travelsTable.setItems(this.travels);
	}

}
