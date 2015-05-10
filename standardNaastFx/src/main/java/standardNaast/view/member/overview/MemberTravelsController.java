package standardNaast.view.member.overview;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.observer.Observer;
import standardNaast.observer.SubjectImpl;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;

public class MemberTravelsController implements Observer {

	private final ObservableList<Season> seasonsList = FXCollections.observableArrayList();

	private final SeasonService seasonService = new SeasonServiceImpl();

	@FXML
	private ComboBox<Season> seasons;

	@FXML
	private TextField home;

	@FXML
	private TextField away;

	@FXML
	private TextField total;

	private long personneId;

	@FXML
	public void initialize() {
		final List<Season> seasons = SubjectImpl.getInstance().getSeasons();
		this.update(seasons);
	}

	@FXML
	public void onSelectedSeason() {
		final Season selectedSeason = this.seasons.getSelectionModel().getSelectedItem();
		final MemberSeasonTravels travelsPerSeason = this.seasonService.getTravelsPerSeason(selectedSeason,
				this.personneId);
		this.home.setText(String.valueOf(travelsPerSeason.getHome()));
		this.away.setText(String.valueOf(travelsPerSeason.getAway()));
		this.total.setText(String.valueOf(travelsPerSeason.getHome() + travelsPerSeason.getAway()));

	}

	public long getPersonneId() {
		return this.personneId;
	}

	public void setPersonneId(final long personneId) {
		this.personneId = personneId;
		this.seasons.setDisable(false);
		this.onSelectedSeason();
	}

	@Override
	public void update(final List<Season> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.seasons.setItems(this.seasonsList);
	}
}
