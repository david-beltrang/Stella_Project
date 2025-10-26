package Infrastructure.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Function;

public class NavigationManager {

    /**
     * Cambia la escena actual usando la factory de controladores central.
     */
    public void goTo(String fxmlPath, String title,
                     Function<Class<?>, Object> controllerFactory,
                     Button refButton) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxmlPath))
            );
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent root = loader.load();
            Stage stage = (Stage) refButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//holapp