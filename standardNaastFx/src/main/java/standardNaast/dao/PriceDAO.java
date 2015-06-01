package standardNaast.dao;

import java.util.List;

import standardNaast.entities.AbonnementPrices;
import standardNaast.entities.Prices;
import standardNaast.entities.Season;
import standardNaast.types.CompetitionType;

public interface PriceDAO {

	void addPrice(Prices price);

	AbonnementPrices getPrice(Long priceId);

	List<CompetitionType> getCompetitionTypePerSeason(Season season);

}
