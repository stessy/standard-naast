package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Season;
import standardNaast.service.SaisonService;

@Named(value = "seasonsBean")
@SessionScoped
public class SeasonBean implements Serializable {

	private static final long serialVersionUID = 7004625731973211539L;

	private Season selectedSeason;

	private List<Season> seasons;

	@Inject
	private SaisonService saisonService;

	@PostConstruct
	public void init() {
		System.out.println("Init SeasonBean");
		List<Season> saisons = this.saisonService.findAllSaison();
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
