package Infrastructure.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Function;

/**
 * Clase auxiliar para gestionar la navegación entre vistas FXML.
 *
 * Permite cambiar de escena desde cualquier nodo visual (Button, Label, TextField, etc.)
 * manteniendo la factory de controladores.
 */
public class NavigationManager {

    /**
     * Cambia la vista actual por la especificada en el path FXML.
     *
     * @param fxmlPath ruta del archivo FXML de destino (ej: "/views/Principal.fxml")
     * @param title título de la nueva ventana
     * @param controllerFactory factory global de controladores (inyectada por ControllerControladores)
     * @param origen cualquier nodo de la vista actual (botón, campo, etc.)
     */
    public void goTo(String fxmlPath, String title, Function<Class<?>, Object> controllerFactory, Node origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            // Obtiene la ventana actual a partir de cualquier nodo visible
            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error al cambiar de vista: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
