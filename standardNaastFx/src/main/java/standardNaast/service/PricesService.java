package standardNaast.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.dao.PriceDAO;
import standardNaast.dao.PriceDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.AbonnementPrices;
import standardNaast.entities.Season;
import standardNaast.model.BlocAbonnementPrice;
import standardNaast.model.SeasonModel;
import standardNaast.types.CompetitionType;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class PricesService {

	private final PriceDAO priceDAO = new PriceDAOImpl();

	private final SeasonDAO seasonDAO = new SeasonDAOImpl();

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	public List<CompetitionType> getConfiguredCompetitionTypePerSeason(final SeasonModel seasonModel) {
		final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
		return this.priceDAO.getCompetitionTypePerSeason(season);
	}

	public List<BlocAbonnementPrice> getAbonnementsPrices(final SeasonModel seasonModel,
			final CompetitionType competitionType) {
		final List<BlocAbonnementPrice> blocAbonnementPrices = new ArrayList<>();
		final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
		final TypedQuery<String> blocsQuery = this.entityManager.createNamedQuery("findBlocsPerSeason",
				String.class);
		blocsQuery.setParameter("season", season);
		blocsQuery.setParameter("competitionType", competitionType);
		final List<String> blocList = blocsQuery.getResultList();
		for (final String bloc : blocList) {
			final BlocAbonnementPrice blocAbonnementPrice = new BlocAbonnementPrice();
			blocAbonnementPrice.setBloc(bloc);
			final TypedQuery<AbonnementPrices> blocPricesquery = this.entityManager.createNamedQuery(
					"getBlocPricesPerSeason",
					AbonnementPrices.class);
			blocPricesquery.setParameter("season", season);
			blocPricesquery.setParameter("competitionType", competitionType);
			blocPricesquery.setParameter("bloc", bloc);
			final List<AbonnementPrices> blocPrices = blocPricesquery.getResultList();
			for (final AbonnementPrices abonnementPrices : blocPrices) {
				switch (abonnementPrices.getTypePersonne()) {
				case ADULT:
					blocAbonnementPrice.setFullPrice(String.valueOf(abonnementPrices.getPrice()));
					break;
				case LESS_THAN_TWELVE:
					blocAbonnementPrice.setLessThanTwelve(String.valueOf(abonnementPrices.getPrice()));
					break;
				case PENSIONED:
					blocAbonnementPrice.setSenior(String.valueOf(abonnementPrices.getPrice()));
					break;
				case STUDENT:
					blocAbonnementPrice.setStudent(String.valueOf(abonnementPrices.getPrice()));
					break;
				case TWELVE_EIGHTEEN:
					blocAbonnementPrice.setTwelveEigtheen(String.valueOf(abonnementPrices.getPrice()));
					break;
				}
			}
			blocAbonnementPrices.add(blocAbonnementPrice);

		}
		return blocAbonnementPrices;
	}

}
