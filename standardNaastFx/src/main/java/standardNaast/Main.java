package standardNaast;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.store.fs.FileUtils;
import org.h2.tools.Script;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import standardNaast.constants.DateFormat;
import standardNaast.utils.DateUtils;
import standardNaast.utils.DbUtils;
import standardNaast.view.member.overview.MembersOverview;

public class Main extends Application {

	public static Main main;

	private static final Logger LOG = LogManager.getLogger(Application.class);

	private static Stage primaryStage;

	private BorderPane rootLayout;

	@Override
	public void start(final Stage stage) {
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

	@Override
	public void stop() {
		final Properties props = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream("init.properties");
			props.load(fis);
			final String databaseFrom = props.getProperty("urlCopy") + props.getProperty("database");
			final String databaseTo = props.getProperty("urlBackup") + "backup_"
					+ DateUtils.formatDate(new Date(), DateFormat.YYYYMMDDHHMMSS);
			System.out.println("Database from: " + databaseFrom);
			System.out.println("Database to: " + databaseTo);
			FileUtils.moveTo(databaseFrom, databaseTo);
			final String url = "jdbc:h2:mem:standard_naast";
			Script.execute(url, "sa",
					null,
					databaseFrom);
		} catch (final IOException | SQLException e) {
			Main.LOG.error("The database could not be backed up on exit due to : ", e);
		}
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
