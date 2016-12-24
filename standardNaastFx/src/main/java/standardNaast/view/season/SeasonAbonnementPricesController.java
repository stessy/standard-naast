package standardNaast.view.season;

import java.io.File;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import standardNaast.model.BlocAbonnementPrice;
import standardNaast.model.SeasonModel;
import standardNaast.service.PricesService;
import standardNaast.types.CompetitionType;
import standardNaast.utils.AbonnementPricesImporter;
import standardNaast.utils.AlertDialogUtils;

public class SeasonAbonnementPricesController {

	private final PricesService pricesService = new PricesService();

	private ObservableList<CompetitionType> competitionTypeList = FXCollections.observableArrayList();

	private ObservableList<BlocAbonnementPrice> abonnementsList = FXCollections.observableArrayList();

	@FXML
	private ComboBox<CompetitionType> competitions;

	@FXML
	private TableView<BlocAbonnementPrice> abonnementPricesTable;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> blocColumn;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> fullPriceColumn;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> studentColumn;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> lessThanTwelveColumn;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> twelveEighteenColumn;

	@FXML
	private TableColumn<BlocAbonnementPrice, String> seniorColumn;

	private SeasonModel selectedSeason;

	@FXML
	private void initialize() {
		this.competitions.setDisable(true);
	}

	public void buildCompetitionTypes(final SeasonModel selectedSeason) {
		this.selectedSeason = selectedSeason;
		this.competitions.setDisable(true);
		final List<CompetitionType> configuredCompetitionTypePerSeason = this.pricesService
				.getConfiguredCompetitionTypePerSeason(selectedSeason);
		this.competitionTypeList.clear();
		this.competitionTypeList.addAll(configuredCompetitionTypePerSeason);
		this.competitions.setItems(this.competitionTypeList);
		this.competitions.setDisable(false);
	}

	@FXML
	private void onSelectedCompetitionType() {
		final CompetitionType selectedCompetitionType = this.competitions.getSelectionModel().getSelectedItem();
		final List<BlocAbonnementPrice> abonnementsPrices = this.pricesService.getAbonnementsPrices(
				this.selectedSeason, selectedCompetitionType);
		this.buildTable(abonnementsPrices);
	}

	private void buildTable(final List<BlocAbonnementPrice> abonnementsPrices) {
		this.abonnementsList.clear();
		this.abonnementsList.addAll(abonnementsPrices);
		this.abonnementPricesTable.setItems(this.abonnementsList);
		this.bindProperties();
	}

	private void bindProperties() {
		this.blocColumn.setCellValueFactory(cellData -> cellData.getValue().blocProperty());
		this.lessThanTwelveColumn.setCellValueFactory(cellData -> cellData.getValue().lessThanTwelveProperty());
		this.twelveEighteenColumn.setCellValueFactory(cellData -> cellData.getValue().twelveEigtheenProperty());
		this.seniorColumn.setCellValueFactory(cellData -> cellData.getValue().seniorProperty());
		this.fullPriceColumn.setCellValueFactory(cellData -> cellData.getValue().fullPriceProperty());
		this.studentColumn.setCellValueFactory(cellData -> cellData.getValue().studentProperty());
	}

	@FXML
	private void onOpen() {
		if (this.selectedSeason == null) {
			AlertDialogUtils.displayErrorAlert(null, "Saison non sélectionnée");
		} else {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			fileChooser.getExtensionFilters().addAll(
					new ExtensionFilter("All Files", "*.*"));
			final File selectedFile = fileChooser.showOpenDialog(null);
			if (selectedFile != null) {
				final AbonnementPricesImporter importer = new AbonnementPricesImporter();
				final boolean success = importer.importAbonnementsPrices(selectedFile.getAbsolutePath(),
						this.selectedSeason,
						CompetitionType.CHAMPIONSHIP);
				if (success) {
					AlertDialogUtils.displaySuccessALert("Prix des abonnements importés");
					this.buildCompetitionTypes(this.selectedSeason);
					this.competitions.getSelectionModel().select(CompetitionType.CHAMPIONSHIP);
				} else {
					AlertDialogUtils.displayErrorAlert(null, "Prix des abonnements non importés");
				}
			}
		}
	}

}
