package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import standardNaast.entities.Season;
import standardNaast.service.SeasonServiceImpl;

@Named(value = "seasonsBean")
@ViewScoped
public class SeasonBean implements Serializable {

	private static final long serialVersionUID = 7004625731973211539L;

	private Season selectedSeason;

	private List<Season> seasons;

	@EJB
	private SeasonServiceImpl saisonService;

	@PostConstruct
	public void init() {
		final List<Season> saisons = this.saisonService.findAllSaison();
		Collections.sort(saisons);
		this.seasons = saisons;
	}

	public List<Season> getSeasons() {
		return this.seasons;
	}

	public Season getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final Season selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

}
