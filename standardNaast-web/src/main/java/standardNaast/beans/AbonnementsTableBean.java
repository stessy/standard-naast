package standardNaast.beans;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Season;
import standardNaast.service.SeasonService;

@Named(value = "abonnementsTable")
@ViewScoped
public class AbonnementsTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	@Inject
	private SeasonService seasonService;

	private List<Season> seasons;

	private Season selectedSeason;

	public AbonnementsTableBean() {
		System.out.println("Instantiating AbonnementsTableBean");
	}

	// Business methods

	public void fillDropdownSeason() {
		this.setSeasons(this.seasonService.findAllSaison());
	}

	// Getters and setters

	public List<Season> getSeasons() {
		return this.seasons;
	}

	public void setSeasons(final List<Season> seasons) {
		this.seasons = seasons;
	}

	public Season getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final Season selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public void setAbonnementsSeason() {

	}

}
