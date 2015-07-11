package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Match;
import standardNaast.entities.PersonneTravel;
import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;

public interface TravelDAO {

	List<TravelPrice> getTravelsPerSeason(Season season);

	List<PersonneTravel> getMatchTravels(Match match);

	PersonneTravel addMemberMatchTravel(PersonneTravel travel);

	void removeMemberMatchTravel(PersonneTravel entity);

}
