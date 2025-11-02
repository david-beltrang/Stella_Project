package Infrastructure.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Function;

public class Navigacion {

    public void goTo(String fxmlPath, String title,
                     Function<Class<?>, Object> controllerFactory,
                     Node origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            // 🔴 para que lo veas EN RUNTIME
            e.printStackTrace();
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "No pude abrir la vista: " + fxmlPath + "\n" + e.getMessage()
            );
            a.showAndWait();
        }
    }
}


