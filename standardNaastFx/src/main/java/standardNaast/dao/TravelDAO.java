package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;

public interface TravelDAO {

	List<TravelPrice> getTravelsPerSeason(Season season);

}
