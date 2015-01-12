/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import standardNaast.constants.DateFormat;
import standardNaast.dao.AbonnementDAO;
import standardNaast.dao.AbonnementDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.AbonnementsModel;
import standardNaast.model.PurchasableAbonnements;
import standardNaast.types.AbonnementStatus;

/**
 * @author stessy
 *
 */
@Stateless
@LocalBean
public class AbonnementService implements Serializable {

	private static final long serialVersionUID = -7174502180535914551L;

	AbonnementDAO abonnementDao = new AbonnementDAOImpl();

	SeasonDAO seasonDao = new SeasonDAOImpl();

	public void setAsPurchased(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsPurchased(abonnement);
		}
	}

	public void setAsNotYetPaid(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsNotYetPaid(abonnement);
		}
	}

	public void setAsReceived(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsReceived(abonnement);
		}
	}

	public void setAsPaid(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsPaid(abonnement);
		}
	}

	public void setAsDistributed(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsDistributed(abonnement);
		}
	}

	public AbonnementsModel getAbonnementsPerSeason(final String selectedSeason) {
		final Season season = this.seasonDao.getSeasonById(selectedSeason);
		final List<Abonnement> abonnementsPerSeason = this.abonnementDao
				.getAbonnementsPerSeason(season);
		final SortedSet<Abonnement> paidAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> unpaidAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> newAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> purchasedAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> receivedAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> distributedAbonnements = new TreeSet<>();

		for (final Abonnement abonnement : abonnementsPerSeason) {
			if (abonnement.getPaye()) {
				paidAbonnements.add(abonnement);
			} else {
				unpaidAbonnements.add(abonnement);
			}
			final AbonnementStatus abonnementStatus = abonnement
					.getAbonnementStatus();
			switch (abonnementStatus) {
			case DISTRIBUTED:
				distributedAbonnements.add(abonnement);
				break;
			case PURCHASED:
				purchasedAbonnements.add(abonnement);
				break;
			case RECEIVED:
				receivedAbonnements.add(abonnement);
				break;
			case NEW:
				newAbonnements.add(abonnement);
				break;
			default:
				throw new IllegalStateException(
						"L'abonnement a un statut inexistant["
								+ abonnementStatus + "]");
			}

		}

		final AbonnementsModel abonnementsModel = new AbonnementsModel();
		abonnementsModel.setDistributedAbonnements(distributedAbonnements);
		abonnementsModel.setNewAbonnements(newAbonnements);
		abonnementsModel.setPaidAbonnements(paidAbonnements);
		abonnementsModel.setPurchasedAbonnements(purchasedAbonnements);
		abonnementsModel.setReceivedAbonnements(receivedAbonnements);
		abonnementsModel.setUnpaidAbonnements(unpaidAbonnements);

		return abonnementsModel;

	}

	public void printAbonnements(final List<Abonnement> abonnementsToPurchase) {
		final List<PurchasableAbonnements> purchasableAbonnements = new ArrayList<>();
		for (final Abonnement abonnement : abonnementsToPurchase) {
			final Personne person = abonnement.getPersonne();
			final Date birthdate = person.getBirthdate();
			final String formattedBirthdate = new SimpleDateFormat(
					DateFormat.DDSMMSYYYY).format(birthdate);
			// final PurchasableAbonnements purchasableAbonnement = new
			// PurchasableAbonnements(person.getName(), person.getFirstname(),
			// birthdate, person.getFullAddress(),
			// person.getIdentityCardNumber(),
			// abonnement.getBlocId().getBlocValue(), abonnement.getRang(),
			// abonnement.getPlace(), amount)
		}
	}
}
