package standardNaast.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.entities.Abonnement;
import standardNaast.types.AbonnementStatus;

public class MemberAbonnementsModel {

	private final LongProperty abonnementId = new SimpleLongProperty();

	private final ObjectProperty<SeasonModel> saison = new SimpleObjectProperty<>();

	private final StringProperty rang = new SimpleStringProperty();

	private final StringProperty place = new SimpleStringProperty();

	private final LongProperty reduction = new SimpleLongProperty();

	private final LongProperty acompte = new SimpleLongProperty();

	private final BooleanProperty paye = new SimpleBooleanProperty();

	private final ObjectProperty<AbonnementStatus> status = new SimpleObjectProperty<>();

	private final ObjectProperty<AbonnementPriceChampionshipModel> abonnementPrice = new SimpleObjectProperty<>();

	public Long getAbonnementId() {
		return this.abonnementId.longValue();
	}

	public void setAbonnementId(final Long abonnementId) {
		this.abonnementId.set(abonnementId);
	}

	public SeasonModel getSaison() {
		return this.saison.get();
	}

	public void setSaison(final SeasonModel saison) {
		this.saison.set(saison);
	}

	public String getRang() {
		return this.rang.get();
	}

	public void setRang(final String rang) {
		this.rang.set(rang);
	}

	public String getPlace() {
		return this.place.get();
	}

	public void setPlace(final String place) {
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

	public Boolean getPaye() {
		return this.paye.get();
	}

	public void setPaye(final Boolean paye) {
		this.paye.set(paye);
	}

	public AbonnementStatus getStatus() {
		return this.status.get();
	}

	public void setStatus(final AbonnementStatus status) {
		this.status.set(status);
	}

	public LongProperty abonnementIdProperty() {
		return this.abonnementId;
	}

	public ObjectProperty<SeasonModel> saisonProperty() {
		return this.saison;
	}

	public StringProperty rangProperty() {
		return this.rang;
	}

	public StringProperty placeProperty() {
		return this.place;
	}

	public LongProperty reductionProperty() {
		return this.reduction;
	}

	public LongProperty acompteProperty() {
		return this.acompte;
	}

	public BooleanProperty payeProperty() {
		return this.paye;
	}

	public ObjectProperty<AbonnementStatus> statusProperty() {
		return this.status;
	}

	public AbonnementPriceChampionshipModel getAbonnementPrice() {
		return this.abonnementPrice.get();
	}

	public void setAbonnementPrice(final AbonnementPriceChampionshipModel abonnementPrice) {
		this.abonnementPrice.set(abonnementPrice);
	}

	public ObjectProperty<AbonnementPriceChampionshipModel> abonnementPriceProperty() {
		return this.abonnementPrice;
	}

	public static MemberAbonnementsModel toModel(final Abonnement abonnement) {
		final MemberAbonnementsModel model = new MemberAbonnementsModel();
		model.setAbonnementId(abonnement.getId());
		model.setAbonnementPrice(AbonnementPriceChampionshipModel.toModel(abonnement.getAbonnementPrice()));
		model.setAcompte(abonnement.getAcompte());
		model.setPaye(abonnement.getPaye());
		model.setPlace(abonnement.getPlace());
		model.setRang(abonnement.getRang());
		model.setReduction(abonnement.getReduction());
		model.setSaison(SeasonModel.of(abonnement.getSaison()));
		model.setStatus(abonnement.getAbonnementStatus());
		return model;
	}

	public static Abonnement toEntity(final MemberAbonnementsModel model) {
		final Abonnement abonnement = new Abonnement();
		abonnement.setAbonnementPrice(AbonnementPriceChampionshipModel.toEntity(model.getAbonnementPrice()));
		abonnement.setAcompte(model.getAcompte());
		abonnement.setPaye(model.getPaye());
		abonnement.setPlace(model.getPlace());
		abonnement.setRang(model.getRang());
		abonnement.setReduction(model.getReduction());
		abonnement.setSaison(SeasonModel.of(model.getSaison()));
		abonnement.setAbonnementStatus(model.getStatus());
		return abonnement;
	}
}
