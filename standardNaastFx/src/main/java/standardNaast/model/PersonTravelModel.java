package standardNaast.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.PersonneTravel;

public class PersonTravelModel {

	private LongProperty id = new SimpleLongProperty();

	private ObjectProperty<PersonModel> person = new SimpleObjectProperty<>();

	private LongProperty amount = new SimpleLongProperty();

	private ObjectProperty<MatchModel> match = new SimpleObjectProperty<>();

	private BooleanProperty paid = new SimpleBooleanProperty();

	public Long getId() {
		return this.id.get();
	}

	public void setId(final Long id) {
		this.id.set(id);
	}

	public PersonModel getPerson() {
		return this.person.get();
	}

	public void setPerson(final PersonModel member) {
		this.person.set(member);
	}

	public Long getAmount() {
		return this.amount.get();
	}

	public void setAmount(final Long amount) {
		this.amount.set(amount);
	}

	public MatchModel getMatch() {
		return this.match.get();
	}

	public void setMatch(final MatchModel match) {
		this.match.set(match);
	}

	public boolean isPaid() {
		return this.paid.get();
	}

	public void setPaid(final boolean paid) {
		this.paid.set(paid);
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public ObjectProperty<PersonModel> personProperty() {
		return this.person;
	}

	public LongProperty amountProperty() {
		return this.amount;
	}

	public ObjectProperty<MatchModel> matchProperty() {
		return this.match;
	}

	public BooleanProperty paidProperty() {
		return this.paid;
	}

	public static PersonneTravel toEntity(final PersonTravelModel model) {
		final PersonneTravel entity = new PersonneTravel();
		entity.setCarTravelAmount(model.getAmount());
		entity.setMatch(MatchModel.toEntity(model.getMatch()));
		entity.setPersonne(PersonModel.toEntity(model.getPerson()));
		entity.setPaid(model.isPaid());
		entity.setId(model.getId());
		return entity;
	}

	public static PersonTravelModel toModel(final PersonneTravel entity) {
		final PersonTravelModel model = new PersonTravelModel();
		model.setAmount(entity.getCarTravelAmount());
		model.setMatch(MatchModel.toModel(entity.getMatch()));
		model.setPerson(PersonModel.toModel(entity.getPersonne()));
		model.setPaid(entity.isPaid());
		model.setId(entity.getId());
		return model;
	}

}
