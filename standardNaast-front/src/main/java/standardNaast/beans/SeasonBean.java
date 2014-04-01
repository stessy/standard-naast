package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Saison;
import standardNaast.service.SaisonService;

@Controller("seasonsBean")
@Scope("session")
public class SeasonBean implements Serializable {

	private static final long serialVersionUID = 7004625731973211539L;

	private Saison selectedSeason;

	private List<Saison> seasons;

	@Autowired
	private SaisonService saisonService;

	@PostConstruct
	public void init() {
		System.out.println("Init SeasonBean");
		List<Saison> saisons = this.saisonService.findAllSaison();
		Collections.sort(saisons);
		this.seasons = saisons;
	}

	public List<Saison> getSeasons() {
		return this.seasons;
	}

	public Saison getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final Saison selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

}
