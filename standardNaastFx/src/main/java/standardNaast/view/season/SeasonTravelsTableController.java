package standardNaast.view.season;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import standardNaast.model.SeasonModel;
import standardNaast.model.TravelModel;
import standardNaast.service.TravelService;
import standardNaast.types.PersonTravelType;
import standardNaast.types.Place;

public class SeasonTravelsTableController {

	private final ObservableList<TravelModel> travels = FXCollections.observableArrayList();

	private final TravelService travelService = new TravelService();

	private TravelModel selectedTravel;

	@FXML
	private TableView<TravelModel> travelsTable;

	@FXML
	private TableColumn<TravelModel, Long> amount;

	@FXML
	private TableColumn<TravelModel, PersonTravelType> category;

	@FXML
	private TableColumn<TravelModel, Place> place;

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

	public void fillTable(final SeasonModel selectedSeason) {
		this.travels.clear();
		final List<TravelModel> travelsPerSeason = this.travelService.getTravelsPricePerSeason(selectedSeason);
		this.travels.addAll(travelsPerSeason);
		this.travelsTable.setItems(this.travels);
		this.bindProperties();
	}

	private void bindProperties() {
		this.member.setCellFactory(CheckBoxTableCell.forTableColumn(this.member));
		this.amount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
		this.category.setCellValueFactory(cellData -> cellData.getValue().typePersonneProperty());
		this.place.setCellValueFactory(cellData -> cellData.getValue().placeProperty());
		this.member.setCellValueFactory(cellData -> cellData.getValue().memberProperty());
	}

}
