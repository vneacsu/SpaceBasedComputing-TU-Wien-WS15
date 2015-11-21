package ws15.sbc.factory.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mozartspaces.core.MzsCoreException;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("Drone factory");
        stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] argv) throws MzsCoreException, InterruptedException {
        launch(argv);
    }
}