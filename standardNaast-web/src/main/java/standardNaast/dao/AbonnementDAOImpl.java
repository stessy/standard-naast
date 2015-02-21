package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Season;
import standardNaast.types.AbonnementStatus;

public class AbonnementDAOImpl implements AbonnementDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void setAsPurchased(final Abonnement abonnement) {
		abonnement.setAbonnementStatus(AbonnementStatus.PURCHASED);
		this.entityManager.merge(abonnement);
	}

	@Override
	public void setAsNotYetPaid(final Abonnement abonnement) {
		abonnement.setPaye(false);
		this.entityManager.merge(abonnement);
	}

	@Override
	public void setAsReceived(final Abonnement abonnement) {
		abonnement.setAbonnementStatus(AbonnementStatus.RECEIVED);
		this.entityManager.merge(abonnement);
	}

	@Override
	public void setAsPaid(final Abonnement abonnement) {
		abonnement.setPaye(true);
		this.entityManager.merge(abonnement);
	}

	@Override
	public void setAsDistributed(final Abonnement abonnement) {
		abonnement.setAbonnementStatus(AbonnementStatus.DISTRIBUTED);
		this.entityManager.merge(abonnement);
	}

	@Override
	public List<Abonnement> getAbonnementsPerSeason(final Season season) {
		final TypedQuery<Abonnement> query = this.entityManager
				.createNamedQuery("getAbonnementsPerSeason", Abonnement.class);
		query.setParameter("season", season);
		return query.getResultList();
	}

}
