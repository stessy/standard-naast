package standardNaast.view.season;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import standardNaast.model.SeasonModel;

public class SeasonInformationController {

	@FXML
	private Label startDate;

	@FXML
	private Label endDate;

	@FXML
	private Label firstMatchDate;

	@FXML
	private Label european;

	@FXML
	private Label cotisationAmount;

	public void onSelectedSeason(final SeasonModel model) {
		this.startDate.setText(model.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		this.endDate.setText(model.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		this.firstMatchDate.setText(model.getFirstMatchDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		this.european.setText(model.isEuropean() ? "OUI" : "NON");
		this.cotisationAmount.setText(String.valueOf(model.getCotisationAMount()));
	}

	public void reset() {
		this.startDate.setText("");
		this.endDate.setText("");
		this.firstMatchDate.setText("");
		this.european.setText("");
		this.cotisationAmount.setText("");
	}

}
