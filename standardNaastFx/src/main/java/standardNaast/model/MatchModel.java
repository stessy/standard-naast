package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.entities.Match;
import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;
import standardNaast.types.PriceType;
import standardNaast.utils.DateUtils;

public class MatchModel {

	private LongProperty id = new SimpleLongProperty();

	private ObjectProperty<TeamModel> opponent = new SimpleObjectProperty<>();

	private ObjectProperty<LocalDate> matchDate = new SimpleObjectProperty<>();

	private ObjectProperty<Place> place = new SimpleObjectProperty<>();

	private ObjectProperty<SeasonModel> season = new SimpleObjectProperty<>();

	private ObjectProperty<CompetitionType> competitionType = new SimpleObjectProperty<>();

	private ObjectProperty<MatchType> matchType = new SimpleObjectProperty<>();

	private ObjectProperty<PriceType> priceType = new SimpleObjectProperty<>();

	public TeamModel getOpponent() {
		return this.opponent.get();
	}

	public void setOpponent(final TeamModel opponent) {
		this.opponent.set(opponent);
	}

	public LocalDate getMatchDate() {
		return this.matchDate.get();
	}

	public void setMatchDate(final LocalDate matchDate) {
		this.matchDate.set(matchDate);
	}

	public Place getPlace() {
		return this.place.get();
	}

	public void setPlace(final Place place) {
		this.place.set(place);
	}

	public SeasonModel getSeason() {
		return this.season.get();
	}

	public void setSeason(final SeasonModel season) {
		this.season.set(season);
	}

	public CompetitionType getCompetitionType() {
		return this.competitionType.get();
	}

	public void setCompetitionType(final CompetitionType competitionType) {
		this.competitionType.set(competitionType);
	}

	public PriceType getPriceType() {
		return this.priceType.get();
	}

	public void setPriceType(final PriceType priceType) {
		this.priceType.set(priceType);
	}

	public MatchType getMatchType() {
		return this.matchType.get();
	}

	public void setMatchType(final MatchType matchType) {
		this.matchType.set(matchType);
	}

	public LongProperty idProperty() {
		return this.id;
	}

	public ObjectProperty<TeamModel> opponentProperty() {
		return this.opponent;
	}

	public ObjectProperty<LocalDate> matchDateProperty() {
		return this.matchDate;
	}

	public ObjectProperty<Place> placeProperty() {
		return this.place;
	}

	public ObjectProperty<SeasonModel> seasonProperty() {
		return this.season;
	}

	public ObjectProperty<CompetitionType> competitionTypeProperty() {
		return this.competitionType;
	}

	public ObjectProperty<PriceType> priceTypeProperty() {
		return this.priceType;
	}

	public ObjectProperty<MatchType> matchTypeProperty() {
		return this.matchType;
	}

	public static MatchModel toModel(final Match entity) {
		final MatchModel model = new MatchModel();
		model.setCompetitionType(entity.getTypeCompetition());
		model.setMatchDate(DateUtils.toLocalDate(entity.getDateMatch()));
		model.setPlace(entity.getPlace());
		model.setPriceType(entity.getPriceType());
		model.setSeason(SeasonModel.of(entity.getSeason()));
		model.setOpponent(TeamModel.toModel(entity.getOpponent()));
		model.setMatchType(entity.getMatchType());
		return model;
	}

	public static Match toEntity(final MatchModel model) {
		final Match match = new Match();
		match.setDateMatch(DateUtils.toDate(model.getMatchDate()));
		match.setMatchType(model.getMatchType());
		match.setOpponent(TeamModel.toEntity(model.getOpponent()));
		match.setPlace(model.getPlace());
		match.setPriceType(model.getPriceType());
		match.setSeason(SeasonModel.of(model.getSeason()));
		match.setTypeCompetition(model.getCompetitionType());
		return match;
	}
}
