package Infrastructure.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.function.Function;

public class HelloController {

    @FXML private Button loginButton;
    @FXML private Button registroButton;

    // Esta referencia se inyecta desde el ControllerControladores
    private Function<Class<?>, Object> controllerFactory;

    // Setter usado por el orquestador para pasarle la factory global
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // --- EVENTOS DE BOTONES ---

    @FXML
    private void goToLogin() {
        cambiarVista("/views/Login.fxml", "STELLA - Login", loginButton);
    }

    @FXML
    private void goToRegistro() {
        cambiarVista("/views/Registro.fxml", "STELLA - Registro", registroButton);
    }

    // --- MÉTODO AUXILIAR DE NAVEGACIÓN ---
    private void cambiarVista(String fxmlPath, String titulo, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();
            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "No se pudo cargar " + fxmlPath + " : " + e.getMessage()
            ).showAndWait();
        }
    }
}
