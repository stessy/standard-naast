package standardNaast.view.customComponent;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import standardNaast.entities.Season;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;

public class SeasonComboBox extends ComboBox<Season> {

	private final SeasonService seasonService = new SeasonServiceImpl();

	private final ObservableList<Season> seasons = FXCollections.observableArrayList();

	private static SeasonComboBox seasonComboBox = null;

	private SeasonComboBox() {
		this.update();
	}

	public static SeasonComboBox getInstance() {
		if (SeasonComboBox.seasonComboBox == null) {
			synchronized (SeasonComboBox.class) {
				if (SeasonComboBox.seasonComboBox == null) {
					SeasonComboBox.seasonComboBox = new SeasonComboBox();
				}
			}
		}
		return SeasonComboBox.seasonComboBox;
	}

	public void update() {
		this.seasons.clear();
		final List<Season> allSeasons = this.seasonService.findAllSaison();
		this.seasons.addAll(allSeasons);
		this.setItems(this.seasons);
	}

}
