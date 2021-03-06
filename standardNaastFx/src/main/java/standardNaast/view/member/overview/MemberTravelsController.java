package standardNaast.view.member.overview;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.model.SeasonModel;
import standardNaast.observer.SeasonObserver;
import standardNaast.observer.SeasonSubjectImpl;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;
import standardNaast.utils.RefundsUtils;

public class MemberTravelsController implements SeasonObserver {

	private final ObservableList<SeasonModel> seasonsList = FXCollections.observableArrayList();

	private final SeasonService seasonService = new SeasonServiceImpl();

	@FXML
	private ComboBox<SeasonModel> seasons;

	@FXML
	private TextField home;

	@FXML
	private TextField away;

	@FXML
	private TextField total;

	@FXML
	private TextField refund;

	private long personneId;

	@FXML
	public void initialize() {
		final List<SeasonModel> seasons = SeasonSubjectImpl.getInstance().getSeasons();
		this.update(seasons);
	}

	@FXML
	public void onSelectedSeason() {
		final SeasonModel selectedSeason = this.seasons.getSelectionModel().getSelectedItem();
		if (selectedSeason != null) {
			final MemberSeasonTravels travelsPerSeason = this.seasonService.getTravelsPerSeason(selectedSeason,
					this.personneId);
			this.home.setText(String.valueOf(travelsPerSeason.getHome()));
			this.away.setText(String.valueOf(travelsPerSeason.getAway()));
			this.total.setText(String.valueOf(travelsPerSeason.getHome() + travelsPerSeason.getAway()));
			final int homeRefund = RefundsUtils.getRefund(travelsPerSeason.getHome());
			final int awayRefund = RefundsUtils.getRefund(travelsPerSeason.getAway());
			this.refund.setText(String.valueOf(homeRefund + awayRefund) + " €");
		}

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
	public void update(final List<SeasonModel> seasons) {
		this.seasonsList.clear();
		this.seasonsList.addAll(seasons);
		this.seasons.setItems(this.seasonsList);
	}
}
