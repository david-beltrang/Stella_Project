package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAccesoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private final DarAccesoService service;

    // Requerido por FXMLLoader cuando hay fx:controller
    public LoginController() {
        this(AppServices.service());
    }

    // Opción con DI si usas setControllerFactory
    public LoginController(DarAccesoService service) {
        this.service = service;
    }

    @FXML private TextField correoField;       // fx:id="correoField" en Login.fxml
    @FXML private PasswordField passwordField; // fx:id="passwordField" en Login.fxml

    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        if (correo == null || correo.isBlank()){
            alert("Falta el correo");
            return;
        }
        if (pass == null || pass.isBlank()) {
            alert("Falta la contraseña");
            return;
        }

        try {
            if (service == null) {
                throw new IllegalStateException(
                        "Servicio no inicializado. Revisa AppServices.init(...) en el arranque."
                );
            }

            // Autentica
            UsuarioResponse u = service.login(new LoginRequest(correo, pass));

            // (Opcional) si manejan sesión global:
            // AppServices.setUsuarioActual(u);

            // Navega a la pantalla principal
            goTo("/views/Principal.fxml");

        } catch (IllegalArgumentException ex) {
            alert("Error de login: " + ex.getMessage());
        } catch (Exception ex) {
            alert("Ups, ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackClicked() {
        goTo("/views/hello-view.fxml");
    }

    @FXML
    private void onForgotClicked() {
        goTo("/views/RecuperarContra.fxml");
    }

    /** Navegación básica: carga el FXML indicado y lo pone en la misma ventana */
    private void goTo(String fxmlPath) {
        try {
            Parent next = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) correoField.getScene().getWindow();
            stage.setScene(new Scene(next, 1920, 1080)); // tamaño fijo si lo necesitas
            stage.centerOnScreen();
        } catch (Exception e) {
            alert("No pude cargar " + fxmlPath + " : " + e.getMessage());
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}
