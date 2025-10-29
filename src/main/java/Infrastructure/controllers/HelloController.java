package Infrastructure.controllers;

import Application.config.AppServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class HelloController {

    @FXML private Button loginButton;
    @FXML private Button registroButton;

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            loader.setControllerFactory(c -> {
                if (c == Infrastructure.controllers.LoginController.class) return new Infrastructure.controllers.LoginController(AppServices.service());
                try { return c.getDeclaredConstructor().newInstance(); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Parent next = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.centerOnScreen();
        } catch (Exception e) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "No pude cargar /views/Login.fxml : " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void goToRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Registro.fxml"));
            loader.setControllerFactory(c -> {
                if (c == Infrastructure.controllers.RegistroController.class)
                    return new Infrastructure.controllers.RegistroController(AppServices.registroService());
                try {
                    return c.getDeclaredConstructor().newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Parent next = loader.load();
            Stage stage = (Stage) registroButton.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No pude cargar Registro.fxml: " + e.getMessage()).showAndWait();
        }
    }
}