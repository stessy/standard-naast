package standardNaast.dao;

import standardNaast.entities.AbonnementPrices;
import standardNaast.entities.Prices;

public interface PriceDAO {

	void addPrice(Prices price);

	AbonnementPrices getPrice(Long priceId);

}
