package standardNaast.dao;

import java.util.List;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;

public interface AbonnementDAO {

	void setAsPurchased(final Abonnement abonnement);

	void setAsNotYetPaid(final Abonnement abonnement);

	void setAsReceived(final Abonnement abonnement);

	void setAsPaid(final Abonnement abonnement);

	void setAsDistributed(final Abonnement abonnement);

	List<Abonnement> getAbonnementsPerSeason(Season season);

	Abonnement getPreviousAbonnement(Season previousSeason, Personne personne);
}
