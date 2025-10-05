package UI.controllers;

import Application.dtos.LoginRequest;
import Application.dtos.UsuarioResponse;
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

    // SE trae desde Main.Stella usando loader.setControllerFactory()
    public LoginController(DarAccesoService service) {
        this.service = service;
    }

    @FXML private TextField correoField;       // fx:id="correoField" en Login.fxml
    @FXML private PasswordField passwordField; // fx:id="passwordField" en Login.fxml

    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        if (correo == null || correo.isBlank()) { alert("Falta el correo"); return; }
        if (pass == null || pass.isBlank())     { alert("Falta la contraseña"); return; }

        try {
            UsuarioResponse u = service.login(new LoginRequest(correo, pass));
            alert("Bienvenido, " + u.nombre() + " (" + u.tipo() + ") - id: " + u.id());

            // Aqui se pone la pantalla a la que se quiere lanzar despues del login
            // goTo("/views/hello-view.fxml");

        } catch (IllegalArgumentException ex) {
            alert("Error de login: " + ex.getMessage());
        } catch (Exception ex) {
            alert("Ups, ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackClicked() {
        // Placeholder de navegación: ajusta la ruta si usas otra vista
        goTo("/views/hello-view.fxml");
    }

    @FXML
    private void onForgotClicked() {
        // Placeholder de navegación: ajusta la ruta si usas otra vista
        goTo("/views/RecuperarContra.fxml");
    }

    /* ---------- helpers ---------- */

    private void goTo(String fxmlPath) {
        try {
            Parent next = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) correoField.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.centerOnScreen();
        } catch (Exception e) {
            alert("No pude cargar " + fxmlPath + " : " + e.getMessage());
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}


