package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BenevolatModel {

	private final LongProperty id = new SimpleLongProperty();

	private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

	private final LongProperty montant = new SimpleLongProperty();

	private final StringProperty description = new SimpleStringProperty();

	public LocalDate getDate() {
		return this.date.get();
	}

	public void setDate(final LocalDate date) {
		this.date.set(date);
	}

	public Long getMontant() {
		return this.montant.get();
	}

	public void setMontant(final Long montant) {
		this.montant.set(montant);
	}

	public String getDescription() {
		return this.description.get();
	}

	public void setDescription(final String descrption) {
		this.description.set(descrption);
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return this.date;
	}

	public LongProperty montantProperty() {
		return this.montant;
	}

	public StringProperty descriptionProperty() {
		return this.description;
	}

	public Long getId() {
		return this.id.get();
	}

	public void setId(final Long id) {
		this.id.set(id);
	}

	public LongProperty idProperty() {
		return this.id;
	}

}
