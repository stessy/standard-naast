package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.AbonnementPrices;
import standardNaast.entities.Prices;
import standardNaast.entities.Season;
import standardNaast.types.CompetitionType;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class PriceDAOImpl implements PriceDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public void addPrice(final Prices price) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(price);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public AbonnementPrices getPrice(final Long priceId) {
		final AbonnementPrices price = this.entityManager.find(AbonnementPrices.class, priceId);
		return price;
	}

	@Override
	public List<CompetitionType> getCompetitionTypePerSeason(final Season season) {
		final TypedQuery<CompetitionType> query = this.entityManager.createNamedQuery("distinctCompetitionType",
				CompetitionType.class);
		query.setParameter("season", season);
		return query.getResultList();
	}

	public List<AbonnementPrices> getAbonnementPricesPerSeason(final Season season,
			final CompetitionType competitionType) {
		final TypedQuery<AbonnementPrices> query = this.entityManager.createNamedQuery("distinctCompetitionType",
				AbonnementPrices.class);
		query.setParameter("season", season);
		query.setParameter("competitionType", competitionType);
		return query.getResultList();
	}

}
