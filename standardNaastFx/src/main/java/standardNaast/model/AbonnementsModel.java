package standardNaast.model;

import java.util.List;

public class AbonnementsModel {

	private List<MemberAbonnementModel> paidAbonnements;

	private List<MemberAbonnementModel> unpaidAbonnements;

	private List<MemberAbonnementModel> newAbonnements;

	private List<MemberAbonnementModel> purchasedAbonnements;

	private List<MemberAbonnementModel> receivedAbonnements;

	private List<MemberAbonnementModel> distributedAbonnements;

	public List<MemberAbonnementModel> getPaidAbonnements() {
		return this.paidAbonnements;
	}

	public void setPaidAbonnements(final List<MemberAbonnementModel> paidAbonnements) {
		this.paidAbonnements = paidAbonnements;
	}

	public List<MemberAbonnementModel> getUnpaidAbonnements() {
		return this.unpaidAbonnements;
	}

	public void setUnpaidAbonnements(
			final List<MemberAbonnementModel> unpaidAbonnements) {
		this.unpaidAbonnements = unpaidAbonnements;
	}

	public List<MemberAbonnementModel> getNewAbonnements() {
		return this.newAbonnements;
	}

	public void setNewAbonnements(final List<MemberAbonnementModel> newAbonnements) {
		this.newAbonnements = newAbonnements;
	}

	public List<MemberAbonnementModel> getPurchasedAbonnements() {
		return this.purchasedAbonnements;
	}

	public void setPurchasedAbonnements(
			final List<MemberAbonnementModel> purchasedAbonnements) {
		this.purchasedAbonnements = purchasedAbonnements;
	}

	public List<MemberAbonnementModel> getReceivedAbonnements() {
		return this.receivedAbonnements;
	}

	public void setReceivedAbonnements(
			final List<MemberAbonnementModel> receivedAbonnements) {
		this.receivedAbonnements = receivedAbonnements;
	}

	public List<MemberAbonnementModel> getDistributedAbonnements() {
		return this.distributedAbonnements;
	}

	public void setDistributedAbonnements(
			final List<MemberAbonnementModel> distributedAbonnements) {
		this.distributedAbonnements = distributedAbonnements;
	}

}
