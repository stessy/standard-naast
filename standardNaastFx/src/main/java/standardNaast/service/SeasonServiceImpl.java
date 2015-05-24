/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
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

	PersonDAO personDAO = new PersonDAOImpl();

	private static final Logger LOGGER = Logger.getLogger(SeasonServiceImpl.class);

	@Override
	public List<SeasonModel> findAllSaison() {
		return this.saisonDAO.getAllSeasons().stream().map(s -> SeasonModel.of(s)).collect(Collectors.toList());
	}

	@Override
	public MemberSeasonTravels getTravelsPerSeason(final SeasonModel season, final long memberId) {
		final Personne person = this.personDAO.getPerson(memberId);
		final Season selectedSeason = this.saisonDAO.getSeasonById(season.getId());
		final MemberSeasonTravels travels = new MemberSeasonTravels();
		travels.setAway(this.saisonDAO.getTravelsPerSeasonAway(selectedSeason, person));
		travels.setHome(this.saisonDAO.getTravelsPerSeasonHome(selectedSeason, person));
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
