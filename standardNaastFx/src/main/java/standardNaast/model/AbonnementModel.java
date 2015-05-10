package standardNaast.model;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Bloc;
import standardNaast.entities.Season;
import standardNaast.types.AbonnementStatus;

public class AbonnementModel {

	private Long memberId;

	private String memberName;

	private String memberFirstName;

	private Season saison;

	private Bloc bloc;

	private Long rang;

	private Long place;

	private Long prixAbonnement;

	private Long reduction;

	private Long acompte;

	private Long solde;

	private AbonnementStatus abonnementStatus;

	public Long getMemberId() {
		return this.memberId;
	}

	public void setMemberId(final Long memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return this.memberName;
	}

	public void setMemberName(final String memberName) {
		this.memberName = memberName;
	}

	public String getMemberFirstName() {
		return this.memberFirstName;
	}

	public void setMemberFirstName(final String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	public Season getSaison() {
		return this.saison;
	}

	public void setSaison(final Season saison) {
		this.saison = saison;
	}

	public Bloc getBloc() {
		return this.bloc;
	}

	public void setBloc(final Bloc bloc) {
		this.bloc = bloc;
	}

	public Long getRang() {
		return this.rang;
	}

	public void setRang(final Long rang) {
		this.rang = rang;
	}

	public Long getPlace() {
		return this.place;
	}

	public void setPlace(final Long place) {
		this.place = place;
	}

	public Long getPrixAbonnement() {
		return this.prixAbonnement;
	}

	public void setPrixAbonnement(final Long prixAbonnement) {
		this.prixAbonnement = prixAbonnement;
	}

	public Long getReduction() {
		return this.reduction;
	}

	public void setReduction(final Long reduction) {
		this.reduction = reduction;
	}

	public Long getAcompte() {
		return this.acompte;
	}

	public void setAcompte(final Long acompte) {
		this.acompte = acompte;
	}

	public Long getSolde() {
		return this.solde;
	}

	public void setSolde(final Long solde) {
		this.solde = solde;
	}

	public AbonnementStatus getAbonnementStatus() {
		return this.abonnementStatus;
	}

	public void setAbonnementStatus(final AbonnementStatus abonnementStatus) {
		this.abonnementStatus = abonnementStatus;
	}

	public Abonnement of(final AbonnementModel model) {
		final Abonnement abonnement = new Abonnement();
		abonnement.setAcompte(this.getAcompte());
		abonnement.setPlace(String.valueOf(this.getPlace()));
		abonnement.setRang(String.valueOf(this.getRang()));
		abonnement.setSaison(this.getSaison());
		abonnement.setBloc(this.getBloc());
		return abonnement;
	}

	public static AbonnementModel of(final Abonnement abonnement) {
		final AbonnementModel model = new AbonnementModel();
		model.setAbonnementStatus(abonnement.getAbonnementStatus());
		model.setAcompte(abonnement.getAcompte());
		model.setBloc(abonnement.getBloc());
		model.setMemberFirstName(abonnement.getPersonne().getFirstname());
		model.setMemberId(abonnement.getPersonne().getPersonneId());
		model.setMemberName(abonnement.getPersonne().getName());
		model.setPlace(Long.valueOf(abonnement.getPlace()));
		model.setRang(Long.valueOf(abonnement.getRang()));
		model.setPrixAbonnement(abonnement.getAbonnementPrice());
		model.setReduction(abonnement.getReduction());
		model.setSolde(abonnement.getAbonnementPrice() - abonnement.getReduction() - abonnement.getAcompte());
		return model;
	}
}
