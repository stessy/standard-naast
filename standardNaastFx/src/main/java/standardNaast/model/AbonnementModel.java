package standardNaast.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.Abonnement;
import standardNaast.entities.Bloc;
import standardNaast.entities.Season;
import standardNaast.types.AbonnementStatus;

public class AbonnementModel {

	private ObjectProperty<PersonModel> person = new SimpleObjectProperty<>();

	private ObjectProperty<SeasonModel> saison = new SimpleObjectProperty<>();

	private Bloc bloc;

	private LongProperty rang = new SimpleLongProperty();

	private LongProperty place = new SimpleLongProperty();

	private LongProperty prixAbonnement = new SimpleLongProperty();

	private LongProperty reduction = new SimpleLongProperty();

	private LongProperty acompte = new SimpleLongProperty();

	private LongProperty solde = new SimpleLongProperty();

	private ObjectProperty<AbonnementStatus> abonnementStatus = new SimpleObjectProperty<>();

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

	public static Abonnement of(final AbonnementModel model) {
		final Abonnement abonnement = new Abonnement();
		abonnement.setAcompte(model.getAcompte());
		abonnement.setPlace(String.valueOf(model.getPlace()));
		abonnement.setRang(String.valueOf(model.getRang()));
		abonnement.setSaison(model.getSaison());
		abonnement.setBloc(model.getBloc());
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
		model.setPrixAbonnement(abonnement.getAbonnementPrice().getPrice());
		model.setReduction(abonnement.getReduction());
		model.setSolde(abonnement.getAbonnementPrice().getPrice() - abonnement.getReduction() - abonnement.getAcompte());
		return model;
	}
}
