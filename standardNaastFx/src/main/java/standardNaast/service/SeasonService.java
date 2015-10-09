package standardNaast.service;

import java.util.List;

import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.model.SeasonModel;

public interface SeasonService {

	List<SeasonModel> findAllSaison();

	MemberSeasonTravels getTravelsPerSeason(SeasonModel season, long memberId);

	Season getSaisonById(String saisonId);

	SeasonModel addSeason(SeasonModel model);

	SeasonModel updateSeason(SeasonModel model);

	SeasonModel getCurrentSeason();

}
