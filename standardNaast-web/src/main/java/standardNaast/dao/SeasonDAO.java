package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Personne;
import standardNaast.entities.Season;

public interface SeasonDAO {

	List<Season> getAllSeasons();

	int getTravelsPerSeasonAway(Season season, Personne member);

	int getTravelsPerSeasonHome(Season season, Personne member);

	Season getSeasonById(String season);

}
