/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.model.SeasonModel;

/**
 * @author stessy
 *
 */
public class SeasonServiceImpl implements SeasonService, Serializable {

	SeasonDAO saisonDAO = new SeasonDAOImpl();

	PersonneService personneService = new PersonneServiceImpl();

	private static final Logger LOGGER = Logger.getLogger(SeasonServiceImpl.class);

	@Override
	public List<Season> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}

	@Override
	public MemberSeasonTravels getTravelsPerSeason(final Season season, final long memberId) {
		final Personne member = this.personneService.getPerson(memberId);
		final MemberSeasonTravels travels = new MemberSeasonTravels();
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(season, member));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(season, member));
		return travels;
	}

	@Override
	public Season getSaisonById(final String saisonId) {
		return this.saisonDAO.getSeasonById(saisonId);
	}

	@Override
	public SeasonModel addSeason(final SeasonModel model) {
		final Season season = SeasonModel.of(model);
		final Season mergedSeason = this.saisonDAO.addSeason(season);
		return SeasonModel.of(mergedSeason);
	}

	@Override
	public SeasonModel updateSeason(final SeasonModel model) {
		final Season matchedSeason = this.saisonDAO.getSeasonById(model.getId());
		SeasonModel.toSeason(model, matchedSeason);
		return SeasonModel.of(this.saisonDAO.merge(matchedSeason));
	}

}
