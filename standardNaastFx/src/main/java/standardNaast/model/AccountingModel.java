package standardNaast.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.entities.Accounting;
import standardNaast.types.AccountingType;
import standardNaast.utils.DateUtils;

public class AccountingModel {

	private LongProperty id = new SimpleLongProperty();

	private ObjectProperty<LocalDate> accountingDate = new SimpleObjectProperty<>();

	private StringProperty description = new SimpleStringProperty();

	private ObjectProperty<AccountingType> type = new SimpleObjectProperty<>();

	private ObjectProperty<BigDecimal> montant = new SimpleObjectProperty<>();

	public Long getId() {
		return this.id.get();
	}

	public void setId(final Long id) {
		this.id.set(id);
	}

	public LocalDate getAccountingDate() {
		return this.accountingDate.get();
	}

	public void setAccountingDate(final LocalDate accountingDate) {
		this.accountingDate.set(accountingDate);
	}

	public String getDescription() {
		return this.description.get();
	}

	public void setDescription(final String description) {
		this.description.set(description);
	}

	public AccountingType getType() {
		return this.type.get();
	}

	public void setType(final AccountingType type) {
		this.type.set(type);
	}

	public BigDecimal getMontant() {
		return this.montant.get();
	}

	public void setMontant(final BigDecimal montant) {
		this.montant.set(montant);
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public ObjectProperty<LocalDate> accountingDateProperty() {
		return this.accountingDate;
	}

	public StringProperty descriptionProperty() {
		return this.description;
	}

	public ObjectProperty<AccountingType> typeProperty() {
		return this.type;
	}

	public ObjectProperty<BigDecimal> montantProperty() {
		return this.montant;
	}

	public static AccountingModel toModel(final Accounting entity) {
		final AccountingModel model = new AccountingModel();
		model.setAccountingDate(DateUtils.toLocalDate(entity.getDate()));
		model.setDescription(entity.getDescription());
		model.setId(entity.getId());
		model.setType(entity.getType());
		model.setMontant(entity.getAmount());
		return model;
	}

	public static Accounting toEntity(final AccountingModel model) {
		final Accounting entity = new Accounting();
		return AccountingModel.toEntity(model, entity);
	}

	public static Accounting toEntity(final AccountingModel model, final Accounting entity) {
		entity.setDate(DateUtils.toDate(model.getAccountingDate()));
		entity.setDescription(model.getDescription());
		entity.setType(model.getType());
		entity.setAmount(model.getMontant());
		return entity;
	}

}
