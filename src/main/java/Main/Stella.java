package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Stella extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));

        // Carga el FXML con el tamaño exacto que definiste (1920x1080)
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Stella App");
        stage.setScene(scene);

        // ⚠️ ESTA ES LA CLAVE - EVITA QUE SE REDIMENSIONE ⚠️
        stage.setResizable(false);

        // En lugar de maximizado, usa tamaño fijo
        stage.setWidth(1920);
        stage.setHeight(1080);

        // Centrar en la pantalla
        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}