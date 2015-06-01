package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.types.AbonnementStatus;
import standardNaast.types.CompetitionType;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class AbonnementDAOImpl implements AbonnementDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	private final SeasonDAO season = new SeasonDAOImpl();

	@Override
	public void setAsPurchased(final Abonnement abonnement) {
		abonnement.setAbonnementStatus(AbonnementStatus.PURCHASED);
		this.entityManager.getTransaction().begin();
		this.season.getSeasonById(abonnement.getSaison().getId());
		this.entityManager.merge(abonnement);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public void setAsNotYetPaid(final Abonnement abonnement) {
		this.entityManager.getTransaction().begin();
		abonnement.setPaye(false);
		this.entityManager.merge(abonnement);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public void setAsReceived(final Abonnement abonnement) {
		this.entityManager.getTransaction().begin();
		abonnement.setAbonnementStatus(AbonnementStatus.RECEIVED);
		this.entityManager.merge(abonnement);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public void setAsPaid(final Abonnement abonnement) {
		this.entityManager.getTransaction().begin();
		abonnement.setPaye(true);
		this.entityManager.merge(abonnement);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public void setAsDistributed(final Abonnement abonnement) {
		this.entityManager.getTransaction().begin();
		abonnement.setAbonnementStatus(AbonnementStatus.DISTRIBUTED);
		this.entityManager.merge(abonnement);
		this.entityManager.getTransaction().commit();
	}

	@Override
	public List<Abonnement> getAbonnementsPerSeason(final Season season) {
		final TypedQuery<Abonnement> query = this.entityManager.createNamedQuery("getAbonnementsPerSeason",
				Abonnement.class);
		query.setParameter("season", season);
		final List<Abonnement> resultList = query.getResultList();
		resultList.stream().forEach(a -> this.entityManager.refresh(a));
		return resultList;
	}

	@Override
	public List<Abonnement> getPreviousAbonnement(final Season previousSeason, final Personne personne,
			final CompetitionType competitionType) {
		final TypedQuery<Abonnement> query = this.entityManager.createNamedQuery("getAbonnementsPreviousSeason",
				Abonnement.class);
		query.setParameter("season", previousSeason);
		query.setParameter("person", personne);
		query.setParameter("competitionType", competitionType);
		return query.getResultList();
	}

	@Override
	public List<Abonnement> getPurchasableAbonnements(final String season) {
		final Season mergedSeason = this.entityManager.find(Season.class, season);
		final TypedQuery<Abonnement> query = this.entityManager.createNamedQuery("getPurchasableAbonnements",
				Abonnement.class);
		query.setParameter("season", mergedSeason);
		return query.getResultList();
	}

}
