package standardNaast.dao;

import java.util.Date;
import java.util.List;

import standardNaast.entities.Personne;
import standardNaast.entities.Season;

public interface SeasonDAO {

	List<Season> getAllSeasons();

	int getTravelsPerSeasonAway(Season season, Personne member);

	int getTravelsPerSeasonHome(Season season, Personne member);

	Season getSeasonById(String season);

	Season addSeason(Season season);

	Season merge(Season season);

	Season getSeasonForSpecificDate(Date date);

}
