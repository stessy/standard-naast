package standardNaast.view.accounting;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.commons.lang3.StringUtils;

import standardNaast.model.AccountingModel;
import standardNaast.service.AccountingService;
import standardNaast.types.AccountingType;
import standardNaast.utils.AlertDialogUtils;

public class AccountingFormController {

	private static final NumberFormat NF = NumberFormat.getInstance(Locale.FRANCE);

	private static final String REGEXP = "[0-9]+([,][0-9]{1,2})?";

	private static final Pattern PATTERN = Pattern.compile(AccountingFormController.REGEXP);

	private AccountingService service = new AccountingService();

	private List<String> validationErrors = new ArrayList<>();

	private ObservableList<AccountingType> typeList = FXCollections.observableArrayList();

	@FXML
	private ComboBox<AccountingType> type;

	@FXML
	private TextField description;

	@FXML
	private DatePicker date;

	@FXML
	private TextField montant;

	private Stage dialogStage;

	private boolean isNew = true;

	private AccountingModel model;

	private AccountingTableController tableController;

	public void fillForm() {
		if (this.model != null && !this.isNew) {
			this.date.setValue(this.model.getAccountingDate());
			this.description.setText(this.model.getDescription());
			this.type.getSelectionModel().select(this.model.getType());
			this.montant.setText(this.model.getMontant().setScale(2).toString());
		}
	}

	@FXML
	private void initialize() {
		final AccountingType[] values = AccountingType.values();
		this.typeList.clear();
		this.typeList.addAll(values);
		this.type.setItems(this.typeList);
	}

	@FXML
	private void onAdd() throws ParseException {
		if (this.isValid()) {
			if (this.isNew) {
				final AccountingModel model = new AccountingModel();
				model.setAccountingDate(this.date.getValue());
				model.setDescription(this.description.getText());
				model.setType(this.type.getSelectionModel().getSelectedItem());
				final Number parse = NumberFormat.getNumberInstance(Locale.FRANCE).parse(this.montant.getText());
				model.setMontant(new BigDecimal(parse.toString()));
				this.service.addAccounting(model);
				this.tableController.onAddedAccounting();
			}
			else {
				this.model.setAccountingDate(this.date.getValue());
				this.model.setDescription(this.description.getText());
				final Number parse = NumberFormat.getNumberInstance(Locale.FRANCE).parse(this.montant.getText());
				this.model.setMontant(new BigDecimal(parse.toString()));
				this.model.setType(this.type.getSelectionModel().getSelectedItem());
				this.service.updateAccounting(this.model);
			}
			this.dialogStage.close();
		}
		else {
			AlertDialogUtils.displayInvalidAlert(this.dialogStage, this.validationErrors);
		}
	}

	@FXML
	private void onCancel() {
		this.dialogStage.close();
	}

	private boolean isValid() {
		this.validationErrors.clear();
		this.validateDate();
		this.validateDescription();
		this.validateType();
		this.validateAmount();
		return this.validationErrors.isEmpty();
	}

	private void validateDate() {
		final LocalDate value = this.date.getValue();
		if (value == null) {
			this.validationErrors.add("Date obligatoire");
		}
	}

	private void validateDescription() {
		final String text = this.description.getText();
		if (StringUtils.isEmpty(text)) {
			this.validationErrors.add("Description obligatoire");
		}
	}

	private void validateType() {
		final int selectedIndex = this.type.getSelectionModel().getSelectedIndex();
		if (selectedIndex < 0) {
			this.validationErrors.add("Type obligatoire");
		}
	}

	private void validateAmount() {
		final String text = this.montant.getText();
		if (StringUtils.isEmpty(text)) {
			this.validationErrors.add("Montant obligatoire");
		}
		final Matcher matcher = AccountingFormController.PATTERN.matcher(text);
		if (!matcher.matches()) {
			this.validationErrors.add("Montant invalide");
		}
	}

	public void setDialogStage(final Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setModel(final AccountingModel model) {
		this.model = model;
	}

	public void setNew(final boolean isNew) {
		this.isNew = isNew;
	}

	public void setController(final AccountingTableController controller) {
		this.tableController = controller;
	}

}
