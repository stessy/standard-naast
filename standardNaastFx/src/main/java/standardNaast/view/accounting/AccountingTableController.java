package standardNaast.view.accounting;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import standardNaast.model.AccountingModel;
import standardNaast.service.AccountingService;
import standardNaast.types.AccountingType;

public class AccountingTableController {

	private ObservableList<AccountingModel> monthlyAccountings = FXCollections.observableArrayList();

	private ObservableList<MonthComboBox> monthList = FXCollections.observableArrayList();

	private ObservableList<YearComboBox> yearList = FXCollections.observableArrayList();

	private AccountingService service = new AccountingService();

	@FXML
	private TableView<AccountingModel> table;

	@FXML
	private TableColumn<AccountingModel, LocalDate> date;

	@FXML
	private TableColumn<AccountingModel, AccountingType> type;

	@FXML
	private TableColumn<AccountingModel, String> description;

	@FXML
	private TableColumn<AccountingModel, BigDecimal> montant;

	@FXML
	private ComboBox<MonthComboBox> month;

	@FXML
	private ComboBox<YearComboBox> year;

	@FXML
	private Button addButton;

	@FXML
	private Button updateButton;

	@FXML
	private TextField monthlyEntries;

	@FXML
	private TextField monthlyExits;

	@FXML
	private TextField monthlyDifference;

	@FXML
	private TextField yearlyEntries;

	@FXML
	private TextField yearlyExits;

	@FXML
	private TextField yearlyDifference;

	@FXML
	private Button refresh;

	private AccountingModel model;

	@FXML
	private void initialize() {
		this.buildComboBoxMonths();
		this.buildComboBoxYear();
		this.filterTable();
		this.bindProperties();
		this.addTableEvent();
		this.calculateYearlyAmounts();
	}

	private void addTableEvent() {
		this.table.setRowFactory(tv -> {
			final TableRow<AccountingModel> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					this.onSelectedRow(row.getItem());
				}
			});
			return row;
		});
	}

	private void buildComboBoxMonths() {
		this.monthList.clear();
		final Month[] values = Month.values();
		final List<Month> asList = Arrays.asList(values);
		final List<MonthComboBox> collect = asList.stream().map(m -> new MonthComboBox(m)).collect(Collectors.toList());
		final MonthComboBox currentMonth = collect.stream().filter(new Predicate<MonthComboBox>() {

			@Override
			public boolean test(final MonthComboBox t) {
				final LocalDate date = LocalDate.now();
				final Month month = date.getMonth();
				return month == t.getMonth();
			}
		}).findFirst().get();
		this.monthList.addAll(collect);
		this.month.setItems(this.monthList);
		this.month.getSelectionModel().select(currentMonth);
	}

	private void buildComboBoxYear() {
		this.yearList.clear();
		final int minAccountingYear = this.service.getMinAccountingYear();
		final int maxAccountingYear = this.service.getMaxAccountingYear();
		for (int i = minAccountingYear; i <= maxAccountingYear; i++) {
			final YearComboBox year = new YearComboBox(Year.of(i));
			this.yearList.add(year);
		}
		this.year.setItems(this.yearList);
		this.year.getSelectionModel().select(0);
	}

	private void buildTableData(final int month, final int year) {
		final List<AccountingModel> accountingsWithinMonth = this.service.getAccountingsWithinMonth(
				month, year);
		this.monthlyAccountings.clear();
		this.monthlyAccountings.addAll(accountingsWithinMonth);
		this.table.setItems(this.monthlyAccountings);
	}

	private void bindProperties() {
		this.date.setCellValueFactory(cellData -> cellData.getValue().accountingDateProperty());
		this.description.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
		this.type.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		this.montant.setCellValueFactory(cellData -> cellData.getValue().montantProperty());
	}

	@FXML
	private void onSelectedMonth() {
		this.filterTable();
		this.calculateMonthlyAmounts();
	}

	@FXML
	private void onSelectedYear() {
		this.filterTable();
		this.calculateMonthlyAmounts();
		this.calculateYearlyAmounts();
	}

	private void filterTable() {
		final int month = this.month.getSelectionModel().getSelectedItem().getMonth().getValue();
		final int year = this.year.getSelectionModel().getSelectedItem().getYear().getValue();
		this.buildTableData(month, year);
	}

	class MonthComboBox {

		private Month month;

		public MonthComboBox(final Month month) {
			this.month = month;
		}

		public Month getMonth() {
			return this.month;
		}

		@Override
		public String toString() {
			return this.month.getDisplayName(TextStyle.FULL, Locale.FRENCH);
		}
	}

	class YearComboBox {

		private Year year;

		public YearComboBox(final Year year) {
			this.year = year;
		}

		public Year getYear() {
			return this.year;
		}

		@Override
		public String toString() {
			return String.valueOf(this.year.getValue());
		}
	}

	private void onSelectedRow(final AccountingModel selectedItem) {
		this.model = selectedItem;
		this.updateButton.setDisable(false);
	}

	@FXML
	private void onAdd() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AccountingFormController.class.getResource("AccountingForm.fxml"));
			final GridPane pane = (GridPane) loader.load();
			final AccountingFormController controller = (AccountingFormController) loader.getController();
			controller.setModel(this.model);
			controller.fillForm();
			controller.setController(this);
			final Stage dialog = new Stage();
			dialog.setTitle("Comptabilité");
			dialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			dialog.setScene(scene);
			controller.setDialogStage(dialog);
			dialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void onUpdate() {
		try {
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AccountingFormController.class.getResource("AccountingForm.fxml"));
			final GridPane pane = (GridPane) loader.load();
			final AccountingFormController controller = (AccountingFormController) loader.getController();
			controller.setModel(this.model);
			controller.setNew(false);
			controller.fillForm();
			controller.setController(this);
			final Stage dialog = new Stage();
			dialog.setTitle("Comptabilité");
			dialog.initModality(Modality.APPLICATION_MODAL);
			final Scene scene = new Scene(pane);
			dialog.setScene(scene);
			controller.setDialogStage(dialog);
			dialog.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void onAddedAccounting() {
		this.buildComboBoxYear();
		this.filterTable();
	}

	private void calculateMonthlyAmounts() {
		final ObservableList<AccountingModel> accountings = this.monthlyAccountings;
		final Amounts amounts = this.calculateAmounts(accountings);
		this.monthlyEntries.setText(amounts.entries.toString());
		this.monthlyExits.setText(amounts.exits.toString());
		this.monthlyDifference.setText(amounts.difference.toString());
	}

	private void calculateYearlyAmounts() {
		final List<AccountingModel> accountingsWithinYear = this.service.getAccountingsWithinYear(this.year
				.getSelectionModel().getSelectedItem().getYear().getValue());
		final Amounts amounts = this.calculateAmounts(accountingsWithinYear);
		this.yearlyEntries.setText(amounts.entries.toString());
		this.yearlyExits.setText(amounts.exits.toString());
		this.yearlyDifference.setText(amounts.difference.toString());
	}

	private Amounts calculateAmounts(final List<AccountingModel> accountings) {
		final Amounts amounts;
		if (!accountings.isEmpty()) {
			final BigDecimal entries = accountings
					.stream()
					.filter(new Predicate<AccountingModel>() {

						@Override
						public boolean test(final AccountingModel t) {
							return t.getType() == AccountingType.ENTRY;
						}
					})
					.map(am -> am.getMontant() != null ? am.getMontant() : BigDecimal.ZERO)
					.reduce((x, y) -> x.add(y))
					.orElse(BigDecimal.ZERO);
			final BigDecimal exits = accountings
					.stream()
					.filter(new
							Predicate<AccountingModel>() {

								@Override
								public boolean test(final AccountingModel t) {
									return t.getType() == AccountingType.EXIT;
								}
							})
					.map(am -> am.getMontant() != null ? am.getMontant() : BigDecimal.ZERO)
					.reduce((x, y) -> x.add(y))
					.orElse(BigDecimal.ZERO);

			amounts = new Amounts(entries, exits);
		} else {
			amounts = new Amounts(BigDecimal.ZERO, BigDecimal.ZERO);
		}

		return amounts;
	}

	@FXML
	private void onRefresh() {
		this.filterTable();
	}

	private class Amounts {

		private final BigDecimal entries;

		private final BigDecimal exits;

		private final BigDecimal difference;

		private Amounts(final BigDecimal entries, final BigDecimal exits) {
			this.entries = entries;
			this.exits = exits;
			this.difference = entries.subtract(exits);
		}

	}
}
