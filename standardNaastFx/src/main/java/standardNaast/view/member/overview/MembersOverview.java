package standardNaast.view.member.overview;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class MembersOverview {

	public MembersOverview() {
		try {
			// Load person overview.
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("MembersOverview.fxml"));
			loader.load();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
