package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.usuario.LoginRequest;
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

    /**
     * ✅ Constructor sin argumentos requerido por FXMLLoader cuando hay fx:controller
     * Toma la instancia desde AppServices (inicializada en Main.Stella con AppServices.init(...)).
     */
    public LoginController() {
        this(AppServices.service()); // puede ser null si no inicializaste AppServices en el arranque
    }

    /**
     * ✅ Constructor con DI por fábrica (setControllerFactory); lo sigues pudiendo usar.
     */
    public LoginController(DarAccesoService service) {
        this.service = service;
    }

    @FXML private TextField correoField;       // fx:id="correoField" en Login.fxml
    @FXML private PasswordField passwordField; // fx:id="passwordField" en Login.fxml

    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass = passwordField.getText();

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
                throw new IllegalStateException("Servicio no inicializado. Revisa AppServices.init(...) en el arranque.");
            }

            UsuarioResponse u = service.login(new LoginRequest(correo, pass));
            alert("Bienvenido, " + u.nombre() + " (" + u.tipo() + ") - id: " + u.id());

            // Si quieres cambiar de pantalla tras login:
            // goTo("/views/hello-view.fxml");

        } catch (IllegalArgumentException ex) {
            alert("Error de login: " + ex.getMessage());
        } catch (Exception ex) {
            alert("Ups, ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackClicked() {
        // Ajusta la ruta si usas otra vista para “volver”
        goTo("/views/hello-view.fxml");
    }

    @FXML
    private void onForgotClicked() {
        // Ir a Recuperar contraseña
        goTo("/views/RecuperarContra.fxml");
    }

    /** Navegación básica: carga por fx:controller usando el constructor sin args */
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
