package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.entities.Season;
import standardNaast.utils.DateUtils;

public class SeasonModel implements Comparable<SeasonModel> {

	private final StringProperty id = new SimpleStringProperty();

	private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();

	private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

	private final ObjectProperty<LocalDate> firstMatchDate = new SimpleObjectProperty<>();

	private final BooleanProperty european = new SimpleBooleanProperty();

	private final LongProperty cotisationAMount = new SimpleLongProperty();

	public String getId() {
		return this.id.get();
	}

	public void setId(final String id) {
		this.id.set(id);
	}

	public LocalDate getStartDate() {
		return this.startDate.get();
	}

	public void setStartDate(final LocalDate startDate) {
		this.startDate.set(startDate);
	}

	public LocalDate getEndDate() {
		return this.endDate.get();
	}

	public void setEndDate(final LocalDate endDate) {
		this.endDate.set(endDate);
	}

	public LocalDate getFirstMatchDate() {
		return this.firstMatchDate.get();
	}

	public void setFirstMatchDate(final LocalDate firstMatchDate) {
		this.firstMatchDate.set(firstMatchDate);
	}

	public Boolean getEuropean() {
		return this.european.get();
	}

	public void setEuropean(final Boolean european) {
		this.european.set(european);
	}

	public Long getCotisationAMount() {
		return this.cotisationAMount.get();
	}

	public void setCotisationAMount(final Long cotisationAMount) {
		this.cotisationAMount.set(cotisationAMount);
	}

	public ObjectProperty<LocalDate> startDateProperty() {
		return this.startDate;
	}

	public ObjectProperty<LocalDate> endDateProperty() {
		return this.endDate;
	}

	public ObjectProperty<LocalDate> firstMatchDateProperty() {
		return this.firstMatchDate;
	}

	public BooleanProperty europeanProperty() {
		return this.european;
	}

	public LongProperty cotisationAmountProperty() {
		return this.cotisationAMount;
	}

	public StringProperty idProperty() {
		return this.id;
	}

	public static Season of(final SeasonModel model) {
		final Season season = new Season();
		season.setDateEnd(DateUtils.toDate(model.getEndDate()));
		season.setDateStart(DateUtils.toDate(model.getStartDate()));
		season.setDateFirstMatchChampionship(DateUtils.toDate(model.getFirstMatchDate()));
		season.setEuropean(model.getEuropean());
		season.setMontantCotisation(model.getCotisationAMount());
		return season;
	}

	public static SeasonModel of(final Season season) {
		final SeasonModel model = new SeasonModel();
		model.setId(season.getId());
		model.setCotisationAMount(season.getMontantCotisation() == null ? 0 : season.getMontantCotisation());
		model.setEndDate(DateUtils.toLocalDate(season.getDateEnd()));
		model.setEuropean(season.isEuropean());
		model.setFirstMatchDate(DateUtils.toLocalDate(season.getDateFirstMatchChampionship()));
		model.setStartDate(DateUtils.toLocalDate(season.getDateStart()));
		return model;
	}

	public static Season toSeason(final SeasonModel model, final Season season) {
		season.setDateEnd(DateUtils.toDate(model.getEndDate()));
		season.setDateStart(DateUtils.toDate(model.getStartDate()));
		season.setDateFirstMatchChampionship(DateUtils.toDate(model.getFirstMatchDate()));
		season.setEuropean(model.getEuropean());
		season.setMontantCotisation(model.getCotisationAMount());
		return season;
	}

	@Override
	public String toString() {
		return this.id.get();
	}

	@Override
	public int compareTo(final SeasonModel o) {
		return this.startDate.get().compareTo(o.startDate.get());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final SeasonModel other = (SeasonModel) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
