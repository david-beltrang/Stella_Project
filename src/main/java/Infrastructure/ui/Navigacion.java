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
public class Navigacion {

     // Cambia la vista actual por la especificada en el path FXML.
     // Parámetros:
     // - fxmlPath: ruta del archivo FXML de la nueva vista (ejemplo: "/views/Principal.fxml")
     // - title: título que se mostrará en la ventana al cambiar de vista
     // - controllerFactory: función usada por FXMLLoader para obtener los controladores existentes
     // - origen: cualquier elemento de la escena actual desde donde se realiza la navegación
    public void goTo(String fxmlPath, String title, Function<Class<?>, Object> controllerFactory, Node origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Si se pasa una factory, se configura para que FXMLLoader use las instancias existentes
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            // Obtiene la ventana actual a partir de cualquier nodo visible
            Stage stage = (Stage) origen.getScene().getWindow();
            // Reemplaza la escena actual con la nueva.
            stage.setScene(new Scene(next));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error al cambiar de vista: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
