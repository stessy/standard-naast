package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MemberCotisationsModel {

	private final ObjectProperty<LocalDate> annee = new SimpleObjectProperty<>();

	private final ObjectProperty<LocalDate> datePaiement = new SimpleObjectProperty<>();

	private final LongProperty montant = new SimpleLongProperty();

	public void setAnnee(final LocalDate annee) {
		this.annee.set(annee);
	}

	public LocalDate getAnnee() {
		return this.annee.get();
	}

	public void setDatePaiement(final LocalDate datePaiement) {
		this.datePaiement.set(datePaiement);
	}

	public LocalDate getDatePaiement() {
		return this.datePaiement.get();
	}

	public void setMontant(final Long montant) {
		this.montant.set(montant);
	}

	public Long getMontant() {
		return this.montant.get();
	}

	public ObjectProperty<LocalDate> anneeProperty() {
		return this.annee;
	}

	public ObjectProperty<LocalDate> datePaimentProperty() {
		return this.datePaiement;
	}

	public LongProperty montantProperty() {
		return this.montant;
	}

}
