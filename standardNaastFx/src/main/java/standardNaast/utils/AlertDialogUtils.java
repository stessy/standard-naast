package standardNaast.utils;

import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AlertDialogUtils {

	public static void displayInvalidAlert(final Stage parentStage, final List<String> errors) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(parentStage);
		alert.setTitle("Erreur de validation");
		alert.setHeaderText("Donnée invalides");
		final StringBuilder sb = new StringBuilder();
		errors.stream().forEach(s -> sb.append(s).append("\n"));
		alert.setContentText(sb.toString());
		alert.showAndWait();
	}

	public static void displaySuccessALert(final String textDoDisplay) {
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(textDoDisplay);

		alert.showAndWait();
	}

	public static void displayErrorAlert(final Stage parentStage, final String error) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(parentStage);
		alert.setTitle("Erreur");
		alert.setHeaderText(null);
		alert.setContentText(error);
		alert.showAndWait();
	}

}
