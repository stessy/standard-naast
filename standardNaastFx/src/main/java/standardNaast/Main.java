package standardNaast;

import com.standardnaast.persistence.EntityManagerFactoryHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import standardNaast.utils.DbUtils;
import standardNaast.view.member.overview.MembersOverview;

import java.io.IOException;

public class Main {

    public static class App extends Application {

        @Override
        public void start(final Stage stage) {
            Main.LOG.warn("Starting Liquibase migration");
            DbUtils.runLiquibase();
            Main.LOG.warn("Starting Application");
            Main.primaryStage = stage;
            stage.setTitle("Standard de Naast");
            Main.main = this;
            Main.initRootLayout();
            Main.showMainView();
            Main.showMembersOverview();
        }

        @Override
        public void stop() {
            try {
                if (EntityManagerFactoryHelper.getFactory() != null && EntityManagerFactoryHelper.getFactory().isOpen()) {
                    EntityManagerFactoryHelper.getFactory().close();
                }
            } catch (final Exception e) {
                Main.LOG.error("The EntityManagerFactory could not be closed on exit: ", e);
            }
        }
    }

    public static App main;

    private static final Logger LOG = LogManager.getLogger(Main.class);

    private static Stage primaryStage;

    private static BorderPane rootLayout;

    /**
     * Initializes the root layout.
     */
    public static void initRootLayout() {
        try {
            // Load root layout from fxml file.
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("RootLayout.fxml"));
            Main.rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            final Scene scene = new Scene(Main.rootLayout);
            Main.primaryStage.setScene(scene);
            Main.primaryStage.show();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMainView() {
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
    public static void showMembersOverview() {
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
        Application.launch(App.class, args);
    }

    public static Application getCurrentApplication() {
        return Main.main;
    }
}
