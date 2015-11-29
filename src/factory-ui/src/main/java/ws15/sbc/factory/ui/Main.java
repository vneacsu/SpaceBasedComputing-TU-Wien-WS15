package ws15.sbc.factory.ui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mozartspaces.core.MzsCoreException;
import ws15.sbc.factory.common.CommonModule;
import ws15.sbc.factory.common.app.AppManager;

import java.io.IOException;

public class Main extends Application {

    private final Injector injector = Guice.createInjector(new CommonModule());

    private final AppManager appManager = injector.getInstance(AppManager.class);

    public Main() {
        appManager.prepareInfrastructure();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/factory-dashboard.fxml"));
        fxmlLoader.setControllerFactory(injector::getInstance);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Drone factory");
        stage.sizeToScene();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        appManager.shutdown();
    }


    public static void main(String[] argv) throws MzsCoreException, InterruptedException {
        launch(argv);
    }
}