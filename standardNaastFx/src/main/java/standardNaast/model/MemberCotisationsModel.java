package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.PersonneCotisation;
import standardNaast.utils.DateUtils;

/**
 * Used to display the list of member's payed cotisations in member overview
 */
public class MemberCotisationsModel {

	private final ObjectProperty<SeasonModel> season = new SimpleObjectProperty<>();

	private final ObjectProperty<LocalDate> datePaiement = new SimpleObjectProperty<>();

	private final LongProperty montant = new SimpleLongProperty();

	public void setSeason(final SeasonModel season) {
		this.season.set(season);
	}

	public SeasonModel getSeason() {
		return this.season.get();
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

	public ObjectProperty<SeasonModel> seasonProperty() {
		return this.season;
	}

	public ObjectProperty<LocalDate> datePaimentProperty() {
		return this.datePaiement;
	}

	public LongProperty montantProperty() {
		return this.montant;
	}

	public static MemberCotisationsModel toModel(final PersonneCotisation entity) {
		final MemberCotisationsModel model = new MemberCotisationsModel();
		model.setSeason(SeasonModel.of(entity.getSeason()));
		model.setMontant(entity.getSeason().getMontantCotisation() != null ? entity.getSeason().getMontantCotisation()
				: 0);
		model.setDatePaiement(DateUtils.toLocalDate(entity.getDatePaiement()));
		return model;
	}

}
