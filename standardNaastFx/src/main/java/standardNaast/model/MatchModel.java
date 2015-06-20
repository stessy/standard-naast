package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import standardNaast.types.CompetitionType;
import standardNaast.types.Place;

public class MatchModel {

	public LongProperty id = new SimpleLongProperty();

	public ObjectProperty<TeamModel> opponent = new SimpleObjectProperty<>();

	public ObjectProperty<LocalDate> matchDate = new SimpleObjectProperty<>();

	public ObjectProperty<Place> place = new SimpleObjectProperty<>();

	public ObjectProperty<SeasonModel> season = new SimpleObjectProperty<>();

	public ObjectProperty<CompetitionType> competitionType = new SimpleObjectProperty<>();

}
