package standardNaast.model;

import java.math.BigDecimal;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.TravelPrice;
import standardNaast.types.PersonType;
import standardNaast.types.Place;

public class TravelModel {

	private ObjectProperty<SeasonModel> season = new SimpleObjectProperty<>();

	private LongProperty amount = new SimpleLongProperty();

	private ObjectProperty<PersonType> typePersonne = new SimpleObjectProperty<>();

	private ObjectProperty<Place> place = new SimpleObjectProperty<>();

	private BooleanProperty member = new SimpleBooleanProperty();

	public ObjectProperty<SeasonModel> seasonProperty() {
		return this.season;
	}

	public LongProperty amountProperty() {
		return this.amount;
	}

	public ObjectProperty<PersonType> typePersonneProperty() {
		return this.typePersonne;
	}

	public ObjectProperty<Place> placeProperty() {
		return this.place;
	}

	public BooleanProperty memberProperty() {
		return this.member;
	}

	public SeasonModel getSeason() {
		return this.season.get();
	}

	public void setSeason(final SeasonModel model) {
		this.season.set(model);
	}

	public Long getAmount() {
		return this.amount.get();
	}

	public void setAmount(final Long amount) {
		this.amount.set(amount);
	}

	public PersonType getTypePersonne() {
		return this.typePersonne.get();
	}

	public void setTypePersonne(final PersonType typePersonne) {
		this.typePersonne.set(typePersonne);
	}

	public Place getPlace() {
		return this.place.get();
	}

	public void setPlace(final Place place) {
		this.place.set(place);
	}

	public Boolean getMember() {
		return this.member.get();
	}

	public void setMember(final Boolean member) {
		this.member.set(member);
	}

	public static TravelPrice toEntity(final TravelModel model) {
		final TravelPrice price = new TravelPrice();
		price.setMembre(model.getMember());
		price.setMontant(BigDecimal.valueOf(model.getAmount()));
		price.setPersonType(model.getTypePersonne());
		price.setPlace(model.getPlace());
		price.setSeason(SeasonModel.of(model.getSeason()));
		return price;
	}

	public static TravelModel toModel(final TravelPrice price) {
		final TravelModel model = new TravelModel();
		model.setSeason(SeasonModel.of(price.getSeason()));
		model.setAmount(price.getMontant().longValue());
		model.setMember(price.isMembre());
		model.setPlace(price.getPlace());
		model.setTypePersonne(price.getPersonType());
		return model;
	}
}
