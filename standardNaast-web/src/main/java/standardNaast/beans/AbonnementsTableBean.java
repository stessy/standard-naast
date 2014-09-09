package standardNaast.beans;

import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Season;
import standardNaast.model.AbonnementsModel;
import standardNaast.service.AbonnementService;
import standardNaast.service.SeasonService;

@Named(value = "abonnementsTable")
@SessionScoped
public class AbonnementsTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	@Inject
	private SeasonService seasonService;

	private List<Season> seasons;

	private String selectedSeason;

	@Inject
	private AbonnementService abonnementService;

	private SortedSet<Abonnement> paidAbonnements;

	private SortedSet<Abonnement> unpaidAbonnements;

	private SortedSet<Abonnement> newAbonnements;

	private SortedSet<Abonnement> purchasedAbonnements;

	private SortedSet<Abonnement> receivedAbonnements;

	private SortedSet<Abonnement> distributedAbonnements;

	private List<Abonnement> selectedPurchasedAbonnements;

	private List<Abonnement> selectedReceivedAbonnements;

	private List<Abonnement> selectedNewAbonnements;

	private List<Abonnement> selectedUnpaidAbonnements;

	public AbonnementsTableBean() {
		System.out.println("Instantiating AbonnementsTableBean");
	}

	// Business methods

	public void fillDropdownSeason() {
		this.setSeasons(this.seasonService.findAllSaison());
	}

	public void setAbonnementsSeason() {
		final AbonnementsModel abonnementsPerSeason = this.abonnementService
				.getAbonnementsPerSeason(this.getSelectedSeason());
		this.setDistributedAbonnements(abonnementsPerSeason
				.getDistributedAbonnements());
		this.setNewAbonnements(abonnementsPerSeason.getNewAbonnements());
		this.setPaidAbonnements(abonnementsPerSeason.getPaidAbonnements());
		this.setPurchasedAbonnements(abonnementsPerSeason
				.getPurchasedAbonnements());
		this.setReceivedAbonnements(abonnementsPerSeason
				.getReceivedAbonnements());
		this.setUnpaidAbonnements(abonnementsPerSeason.getUnpaidAbonnements());
	}

	public void setAbonnementsAsPurchased() {
		this.abonnementService.setAsPurchased(this.selectedNewAbonnements);
		this.setAbonnementsSeason();
	}

	public void setAbonnementsAsReceived() {
		this.abonnementService.setAsReceived(this.selectedPurchasedAbonnements);
		this.setAbonnementsSeason();
	}

	public void setAbonnementsAsDistributed() {
		this.abonnementService
				.setAsDistributed(this.selectedReceivedAbonnements);
		this.setAbonnementsSeason();
	}

	public void setAbonnementsAsPaid() {
		this.abonnementService.setAsPaid(this.selectedUnpaidAbonnements);
		this.setAbonnementsSeason();
	}

	// Getters and setters

	public List<Season> getSeasons() {
		return this.seasons;
	}

	public void setSeasons(final List<Season> seasons) {
		this.seasons = seasons;
	}

	public String getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final String selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public SortedSet<Abonnement> getPaidAbonnements() {
		return this.paidAbonnements;
	}

	public void setPaidAbonnements(final SortedSet<Abonnement> paidAbonnements) {
		this.paidAbonnements = paidAbonnements;
	}

	public SortedSet<Abonnement> getUnpaidAbonnements() {
		return this.unpaidAbonnements;
	}

	public void setUnpaidAbonnements(
			final SortedSet<Abonnement> unpaidAbonnements) {
		this.unpaidAbonnements = unpaidAbonnements;
	}

	public SortedSet<Abonnement> getNewAbonnements() {
		return this.newAbonnements;
	}

	public void setNewAbonnements(final SortedSet<Abonnement> newAbonnements) {
		this.newAbonnements = newAbonnements;
	}

	public SortedSet<Abonnement> getPurchasedAbonnements() {
		return this.purchasedAbonnements;
	}

	public void setPurchasedAbonnements(
			final SortedSet<Abonnement> purchasedAbonnements) {
		this.purchasedAbonnements = purchasedAbonnements;
	}

	public SortedSet<Abonnement> getReceivedAbonnements() {
		return this.receivedAbonnements;
	}

	public void setReceivedAbonnements(
			final SortedSet<Abonnement> receivedAbonnements) {
		this.receivedAbonnements = receivedAbonnements;
	}

	public SortedSet<Abonnement> getDistributedAbonnements() {
		return this.distributedAbonnements;
	}

	public void setDistributedAbonnements(
			final SortedSet<Abonnement> distributedAbonnements) {
		this.distributedAbonnements = distributedAbonnements;
	}

	public List<Abonnement> getSelectedPurchasedAbonnements() {
		return this.selectedPurchasedAbonnements;
	}

	public void setSelectedPurchasedAbonnements(
			final List<Abonnement> selectedPurchasedAbonnements) {
		this.selectedPurchasedAbonnements = selectedPurchasedAbonnements;
	}

	public List<Abonnement> getSelectedReceivedAbonnements() {
		return this.selectedReceivedAbonnements;
	}

	public void setSelectedReceivedAbonnements(
			final List<Abonnement> selectedReceivedAbonnements) {
		this.selectedReceivedAbonnements = selectedReceivedAbonnements;
	}

	public List<Abonnement> getSelectedNewAbonnements() {
		return this.selectedNewAbonnements;
	}

	public void setSelectedNewAbonnements(
			final List<Abonnement> selectedNewAbonnements) {
		this.selectedNewAbonnements = selectedNewAbonnements;
	}

	public List<Abonnement> getSelectedUnpaidAbonnements() {
		return this.selectedUnpaidAbonnements;
	}

	public void setSelectedUnpaidAbonnements(
			final List<Abonnement> selectedUnpaidAbonnements) {
		this.selectedUnpaidAbonnements = selectedUnpaidAbonnements;
	}

}
