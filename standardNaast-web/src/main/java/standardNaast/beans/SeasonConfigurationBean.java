package standardNaast.beans;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;

import standardNaast.entities.Season;

import com.vaadin.cdi.ViewScoped;

@ViewScoped
public class SeasonConfigurationBean {

	private Season selectedSeason;

	private Date dateStart;

	private Date dateEnd;

	private Date firstMatch;

	private boolean european;

	private BigDecimal cotisationAmount;

	@Inject
	private SeasonBean seasons;

	public Season getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final Season selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public Date getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(final Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(final Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getFirstMatch() {
		return this.firstMatch;
	}

	public void setFirstMatch(final Date firstMatch) {
		this.firstMatch = firstMatch;
	}

	public boolean isEuropean() {
		return this.european;
	}

	public void setEuropean(final boolean european) {
		this.european = european;
	}

	public BigDecimal getCotisationAmount() {
		return this.cotisationAmount;
	}

	public void setCotisationAmount(final BigDecimal cotisationAmount) {
		this.cotisationAmount = cotisationAmount;
	}

	public SeasonBean getSeasons() {
		return this.seasons;
	}

	public void setSeasons(final SeasonBean seasons) {
		this.seasons = seasons;
	}

}
