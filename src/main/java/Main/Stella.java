package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Stella extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // CORRECCIÓN CLAVE: Usar la ruta absoluta del classpath
        // "/views/" apunta a la carpeta 'views' dentro de 'resources'
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/hello-view.fxml"));

        // Carga el FXML
        Scene scene = new Scene(fxmlLoader.load());

        // Opcional: Cargar el CSS con ruta absoluta
        scene.getStylesheets().add(getClass().getResource("/styles/LoginStyle.css").toExternalForm());

        stage.setTitle("Stella App");
        stage.setScene(scene);

        // Ajustes de tamaño y ventana
        stage.setResizable(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}