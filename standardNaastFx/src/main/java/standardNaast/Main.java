package standardNaast;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.tools.Backup;

import standardNaast.utils.DbUtils;
import standardNaast.view.member.overview.MembersOverview;

public class Main extends Application {

	public static Main main;

	private static final Logger LOG = LogManager.getLogger(Application.class);

	private static Stage primaryStage;

	private BorderPane rootLayout;

	@Override
	public void start(final Stage stage) {
		Main.LOG.warn("Backing up database before continuing");
		try {
			Backup.execute("..\\database\\backup\\backup", "..\\database\\h2\\dbs", "standard_naast", false);
		} catch (final SQLException e) {
			Main.LOG.error("The database could not be backed up due to : ", e);
			Platform.exit();
		}
		Main.LOG.warn("Database backed up");
		Main.LOG.warn("Starting Liquibase migration");
		DbUtils.runLiquibase();
		Main.LOG.warn("Starting Application");
		Main.primaryStage = stage;
		stage.setTitle("Standard de Naast");
		Main.main = this;
		this.initRootLayout();
		this.showMainView();
		this.showMembersOverview();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("RootLayout.fxml"));
			this.rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			final Scene scene = new Scene(this.rootLayout);
			Main.primaryStage.setScene(scene);
			Main.primaryStage.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void showMainView() {
		// Will contain the menu and the tabs
		try {
			// Load main view from fxml file.
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("MainView.fxml"));
			loader.load();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showMembersOverview() {
		new MembersOverview();
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public static Stage getPrimaryStage() {
		return Main.primaryStage;
	}

	public static void main(final String[] args) {
		Application.launch(args);
	}

	public static Application getCurrentApplication() {
		return Main.main;
	}
}
