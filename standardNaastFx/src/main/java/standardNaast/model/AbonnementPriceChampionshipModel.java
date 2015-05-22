package standardNaast.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import standardNaast.entities.AbonnementPrices;
import standardNaast.types.PersonType;

public class AbonnementPriceChampionshipModel {

	private LongProperty id = new SimpleLongProperty();

	private ObjectProperty<SeasonModel> season = new SimpleObjectProperty<>();

	private LongProperty price = new SimpleLongProperty();

	private ObjectProperty<PersonType> personType = new SimpleObjectProperty<>();

	private StringProperty bloc = new SimpleStringProperty();

	public Long getId() {
		return this.id.get();
	}

	public void setId(final Long id) {
		this.id.set(id);
	}

	public SeasonModel getSeason() {
		return this.season.get();
	}

	public void setSeason(final SeasonModel season) {
		this.season.set(season);
	}

	public Long getPrice() {
		return this.price.get();
	}

	public void setPrice(final Long price) {
		this.price.set(price);
	}

	public PersonType getPersonType() {
		return this.personType.get();
	}

	public void setPersonType(final PersonType personType) {
		this.personType.set(personType);
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public ObjectProperty<SeasonModel> seasonProperty() {
		return this.season;
	}

	public LongProperty priceProperty() {
		return this.price;
	}

	public ObjectProperty<PersonType> personTypeProperty() {
		return this.personType;
	}

	public StringProperty blocProperty() {
		return this.bloc;
	}

	public void setBloc(final String bloc) {
		this.bloc.set(bloc);
	}

	public String getBloc() {
		return this.bloc.get();
	}

	public static AbonnementPriceChampionshipModel toModel(final AbonnementPrices price) {
		final AbonnementPriceChampionshipModel model = new AbonnementPriceChampionshipModel();
		model.setBloc(price.getBloc());
		model.setId(price.getId());
		model.setPersonType(price.getTypePersonne());
		model.setPrice(price.getPrice());
		model.setSeason(SeasonModel.of(price.getSeason()));
		return model;
	}

	public static AbonnementPrices toEntity(final AbonnementPriceChampionshipModel model) {
		final AbonnementPrices price = new AbonnementPrices();
		price.setBloc(model.getBloc());
		price.setTypePersonne(model.getPersonType());
		price.setPrice(model.getPrice());
		price.setSeason(SeasonModel.of(model.getSeason()));
		return price;
	}

}
