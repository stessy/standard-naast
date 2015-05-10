package standardNaast.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.types.AbonnementStatus;

public class MemberAbonnementsModel {

	private final LongProperty abonnementId = new SimpleLongProperty();

	private final StringProperty saison = new SimpleStringProperty();

	private final StringProperty bloc = new SimpleStringProperty();

	private final StringProperty rang = new SimpleStringProperty();

	private final StringProperty place = new SimpleStringProperty();

	private final LongProperty reduction = new SimpleLongProperty();

	private final LongProperty acompte = new SimpleLongProperty();

	private final BooleanProperty paye = new SimpleBooleanProperty();

	private final ObjectProperty<AbonnementStatus> status = new SimpleObjectProperty<>();

	public Long getAbonnementId() {
		return this.abonnementId.longValue();
	}

	public void setAbonnementId(final Long abonnementId) {
		this.abonnementId.set(abonnementId);
	}

	public String getSaison() {
		return this.saison.get();
	}

	public void setSaison(final String saison) {
		this.saison.set(saison);
	}

	public String getBloc() {
		return this.bloc.get();
	}

	public void setBloc(final String bloc) {
		this.bloc.set(bloc);
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

	public StringProperty saisonProperty() {
		return this.saison;
	}

	public StringProperty blocProperty() {
		return this.bloc;
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

}
