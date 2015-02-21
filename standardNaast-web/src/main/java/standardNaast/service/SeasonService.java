package standardNaast.service;

import java.util.List;

import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;

public interface SeasonService {

	List<Season> findAllSaison();

	MemberSeasonTravels getTravelsPerSeason(String season, Personne member);

	Season getSaisonById(String saisonId);

}
