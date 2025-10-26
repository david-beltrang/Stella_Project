package Infrastructure.controllers;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.config.AppServices;
import Application.services.DarAcceso.LoginService;
import Application.services.DarAcceso.RegistroService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegistroController {

    private final RegistroService service;

    // Se inyecta con controllerFactory (desde HelloController.goToRegistro)
    public RegistroController(RegistroService service) {
        this.service = service;
    }

    @FXML private TextField nombreField;
    @FXML private TextField usernameField;
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    private void onRegistrarClicked() {
        String nombre   = text(nombreField);
        String username = text(usernameField);
        String correo   = text(correoField);
        String pass1    = text(passwordField);
        String pass2    = text(confirmPasswordField);

        if (nombre.isBlank() || username.isBlank() || correo.isBlank() || pass1.isBlank() || pass2.isBlank()) {
            alert(Alert.AlertType.WARNING, "Completa todos los campos.");
            return;
        }
        if (!pass1.equals(pass2)) {
            alert(Alert.AlertType.WARNING, "Las contraseñas no coinciden.");
            return;
        }

        try {
            // Por defecto registramos como ESTUDIANTE (coincide con tu VO Tipo)
            RegistrarUsuarioRequest req = new RegistrarUsuarioRequest(
                    username, correo, nombre, pass1, "ESTUDIANTE"
            );

            UsuarioResponse u = service.registrar(req);
            alert(Alert.AlertType.INFORMATION, "✅ Registro exitoso: " + u.nombre());

            // Llevar a Login (inyectando service)
            goToLogin();

        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, "No se pudo registrar: " + ex.getMessage());
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Error inesperado: " + ex.getMessage());
        }
    }

    @FXML
    private void onVolverClicked() {
        goTo("/views/hello-view.fxml", false);
    }

    /* --------- helpers ---------- */

    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            loader.setControllerFactory(c -> {
                if (c == LoginController.class)
                    return new LoginController(AppServices.service());
                try {
                    return c.getDeclaredConstructor().newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Parent next = loader.load();
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.centerOnScreen();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "No pude cargar Login.fxml: " + e.getMessage());
        }
    }

    private void goTo(String fxmlPath, boolean injectService) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (injectService) {
                loader.setControllerFactory(c -> {
                    if (c == LoginController.class)
                        return new LoginController(AppServices.service());
                    try {
                        return c.getDeclaredConstructor().newInstance();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            Parent next = loader.load();
            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.centerOnScreen();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "No pude cargar " + fxmlPath + ": " + e.getMessage());
        }
    }

    private static String text(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }
    private static void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}

