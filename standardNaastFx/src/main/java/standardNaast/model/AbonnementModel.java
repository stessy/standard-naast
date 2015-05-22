package standardNaast.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.Abonnement;
import standardNaast.types.AbonnementStatus;

public class AbonnementModel {

	private ObjectProperty<PersonModel> person = new SimpleObjectProperty<>();

	private ObjectProperty<SeasonModel> saison = new SimpleObjectProperty<>();

	private ObjectProperty<AbonnementPriceChampionshipModel> price = new SimpleObjectProperty<>();

	private LongProperty rang = new SimpleLongProperty();

	private LongProperty place = new SimpleLongProperty();

	private LongProperty reduction = new SimpleLongProperty();

	private LongProperty acompte = new SimpleLongProperty();

	private LongProperty solde = new SimpleLongProperty();

	private ObjectProperty<AbonnementStatus> abonnementStatus = new SimpleObjectProperty<>();

	public PersonModel getPerson() {
		return this.person.get();
	}

	public void setPerson(final PersonModel person) {
		this.person.set(person);
	}

	public SeasonModel getSaison() {
		return this.saison.get();
	}

	public void setSaison(final SeasonModel saison) {
		this.saison.set(saison);
	}

	public AbonnementPriceChampionshipModel getPrice() {
		return this.price.get();
	}

	public void setPrice(final AbonnementPriceChampionshipModel price) {
		this.price.set(price);
	}

	public Long getRang() {
		return this.rang.get();
	}

	public void setRang(final Long rang) {
		this.rang.set(rang);
	}

	public Long getPlace() {
		return this.place.get();
	}

	public void setPlace(final Long place) {
		this.place.set(place);
	}

	public Long getReduction() {
		return this.reduction.get();
	}

	public void setReduction(final Long reduction) {
		this.reduction.set(reduction);
	}

	public Long getAcompte() {
		return this.acompte.get();
	}

	public void setAcompte(final Long acompte) {
		this.acompte.set(acompte);
	}

	public Long getSolde() {
		return this.solde.get();
	}

	public void setSolde(final Long solde) {
		this.solde.set(solde);
	}

	public AbonnementStatus getAbonnementStatus() {
		return this.abonnementStatus.get();
	}

	public void setAbonnementStatus(final AbonnementStatus abonnementStatus) {
		this.abonnementStatus.set(abonnementStatus);
	}

	public ObjectProperty<PersonModel> personProperty() {
		return this.person;
	}

	public ObjectProperty<SeasonModel> saisonProperty() {
		return this.saison;
	}

	public ObjectProperty<AbonnementPriceChampionshipModel> priceProperty() {
		return this.price;
	}

	public LongProperty rangProperty() {
		return this.rang;
	}

	public LongProperty placeProperty() {
		return this.place;
	}

	public LongProperty reductionProperty() {
		return this.reduction;
	}

	public LongProperty acompteProperty() {
		return this.acompte;
	}

	public LongProperty soldeProperty() {
		return this.solde;
	}

	public ObjectProperty<AbonnementStatus> abonnementStatusProperty() {
		return this.abonnementStatus;
	}

	public static Abonnement of(final AbonnementModel model) {
		final Abonnement abonnement = new Abonnement();
		abonnement.setAcompte(model.getAcompte());
		abonnement.setPlace(String.valueOf(model.getPlace()));
		abonnement.setRang(String.valueOf(model.getRang()));
		abonnement.setSaison(model.getSaison());
		return abonnement;
	}

	public static AbonnementModel of(final Abonnement abonnement) {
		final AbonnementModel model = new AbonnementModel();
		model.setAbonnementStatus(abonnement.getAbonnementStatus());
		model.setAcompte(abonnement.getAcompte());
		model.setBloc(abonnement.getBloc());
		model.setPerson(abonnement.getPersonne());
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
