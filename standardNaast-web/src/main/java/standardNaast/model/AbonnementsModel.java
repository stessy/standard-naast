package standardNaast.model;

import java.util.SortedSet;

import standardNaast.entities.Abonnement;

public class AbonnementsModel {

	private SortedSet<Abonnement> paidAbonnements;

	private SortedSet<Abonnement> unpaidAbonnements;

	private SortedSet<Abonnement> newAbonnements;

	private SortedSet<Abonnement> purchasedAbonnements;

	private SortedSet<Abonnement> receivedAbonnements;

	private SortedSet<Abonnement> distributedAbonnements;

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

}
