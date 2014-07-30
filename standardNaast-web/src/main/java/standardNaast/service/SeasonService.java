/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.dao.SaisonDAO;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;

/**
 * @author stessy
 * 
 */
@Stateless
@LocalBean
public class SeasonService implements Serializable {

	@Inject
	SaisonDAO saisonDAO;

	private static final Logger LOGGER = Logger.getLogger(SeasonService.class);

	public List<Season> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}

	public MemberSeasonTravels getTravelsPerSeason(String season,
			Personne member) {
		MemberSeasonTravels travels = new MemberSeasonTravels();
		Season saison = this.saisonDAO.getSeasonById(season);
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(saison, member));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(saison, member));
		return travels;
	}

	public Season getSaisonById(String saisonId) {
		return this.saisonDAO.getSeasonById(saisonId);
	}

}
