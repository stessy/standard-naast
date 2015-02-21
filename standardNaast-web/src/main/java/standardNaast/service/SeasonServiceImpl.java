/**
 *
 */
package standardNaast.service;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import standardNaast.dao.SeasonDAO;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;

/**
 * @author stessy
 *
 */
@Stateless
@LocalBean
public class SeasonServiceImpl implements SeasonService {

	@Inject
	SeasonDAO saisonDAO;

	private static final Logger LOGGER = Logger
			.getLogger(SeasonServiceImpl.class);

	@Override
	public List<Season> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}

	@Override
	public MemberSeasonTravels getTravelsPerSeason(final String season,
			final Personne member) {
		final MemberSeasonTravels travels = new MemberSeasonTravels();
		final Season saison = this.saisonDAO.getSeasonById(season);
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(saison, member));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(saison, member));
		return travels;
	}

	@Override
	public Season getSaisonById(final String saisonId) {
		return this.saisonDAO.getSeasonById(saisonId);
	}

}
