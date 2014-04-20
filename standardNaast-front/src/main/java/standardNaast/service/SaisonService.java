/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.beans.Travels;
import standardNaast.dao.SaisonDAO;
import standardNaast.entities.Personne;
import standardNaast.entities.Saison;

/**
 * @author stessy
 * 
 */
// @Named
// @Service("saisonService")
// @Transactional(readOnly = true)
public class SaisonService implements Serializable {

	// @Autowired
	@Inject
	SaisonDAO saisonDAO;

	private static final Logger LOGGER = Logger.getLogger(SaisonService.class);

	public List<Saison> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}

	public Travels getTravelsPerSeason(String season, Personne member) {
		Travels travels = new Travels();
		Saison saison = this.saisonDAO.getSeasonById(season);
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(saison, member));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(saison, member));
		return travels;
	}

	public Saison getSaisonById(String saisonId) {
		return this.saisonDAO.getSeasonById(saisonId);
	}

}
