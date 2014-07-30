package standardNaast.model;

import java.util.List;

import standardNaast.entities.Abonnement;

public class AbonnementsModel {

	private List<Abonnement> paidAbonnementList;

	private List<Abonnement> unpaidAbonnementList;

	private List<Abonnement> notYetRenewedAbonnements;

	public List<Abonnement> getPaidAbonnementList() {
		return this.paidAbonnementList;
	}

	public void setPaidAbonnementList(final List<Abonnement> paidAbonnementList) {
		this.paidAbonnementList = paidAbonnementList;
	}

	public List<Abonnement> getUnpaidAbonnementList() {
		return this.unpaidAbonnementList;
	}

	public void setUnpaidAbonnementList(
			final List<Abonnement> unpaidAbonnementList) {
		this.unpaidAbonnementList = unpaidAbonnementList;
	}

	public List<Abonnement> getNotYetRenewedAbonnements() {
		return this.notYetRenewedAbonnements;
	}

	public void setNotYetRenewedAbonnements(
			final List<Abonnement> notYetRenewedAbonnements) {
		this.notYetRenewedAbonnements = notYetRenewedAbonnements;
	}
}
