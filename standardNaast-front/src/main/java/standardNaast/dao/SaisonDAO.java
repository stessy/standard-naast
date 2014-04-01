package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Personne;
import standardNaast.entities.Saison;

public interface SaisonDAO {

	List<Saison> getAllSeasons();

	int getTravelsPerSeasonAway(Saison season, Personne member);

	int getTravelsPerSeasonHome(Saison season, Personne member);

	Saison getSeasonById(String season);

}
