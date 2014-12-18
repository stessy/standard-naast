/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
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

	private static final long serialVersionUID = 8960211172323308502L;

	SeasonDAO saisonDAO = new SeasonDAOImpl();

	private static final Logger LOGGER = Logger.getLogger(SeasonService.class);

	public List<Season> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}

	public MemberSeasonTravels getTravelsPerSeason(final String season,
			final Personne member) {
		final MemberSeasonTravels travels = new MemberSeasonTravels();
		final Season saison = this.saisonDAO.getSeasonById(season);
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(saison, member));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(saison, member));
		return travels;
	}

	public Season getSaisonById(final String saisonId) {
		return this.saisonDAO.getSeasonById(saisonId);
	}

}
