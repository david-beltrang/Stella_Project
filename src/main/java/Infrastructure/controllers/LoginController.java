package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Function;

public class LoginController {

    // ===== Dependencias de negocio =====
    private final LoginService service;

    // ===== Navegación (inyectada) =====
    private Function<Class<?>, Object> controllerFactory;

    // Setter para que el orquestador (ControllerControladores) inyecte la factory global
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== Constructores =====

    // Constructor DI: se usa cuando ControllerControladores crea el controlador
    public LoginController(LoginService service) {
        this.service = service;
    }

    // Constructor vacío de respaldo:
    // Esto permite que el FXMLLoader no explote si por accidente
    // alguien lo carga SIN usar controllerFactory.
    // Usa AppServices como fallback para no quedar con 'service' en null.
    public LoginController() {
        this.service = AppServices.service();
    }

    // ===== Referencias a la vista (inyectadas por FXML) =====
    @FXML private TextField correoField;       // fx:id="correoField" en Login.fxml
    @FXML private PasswordField passwordField; // fx:id="passwordField" en Login.fxml

    // ===== Lógica de interacción con la vista =====
    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        if (correo == null || correo.isBlank()) {
            alertError("Falta el correo");
            return;
        }
        if (pass == null || pass.isBlank()) {
            alertError("Falta la contraseña");
            return;
        }

        try {
            if (service == null) {
                // Esto solo debería pasar si nadie inyectó ni AppServices ni el orquestador
                throw new IllegalStateException(
                        "LoginService no inicializado. Revisa AppServices.init(...) o ControllerControladores."
                );
            }

            // Ejecuta el caso de uso de login
            UsuarioResponse usuario = service.login(new LoginRequest(correo, pass));

            // (Opcional) Guardar sesión global:
            // AppServices.setUsuarioActual(usuario);

            // Ir a la pantalla principal
            cambiarVista("/views/Principal.fxml", "STELLA - Principal");

        } catch (IllegalArgumentException ex) {
            alertError("Error de login: " + ex.getMessage());
        } catch (Exception ex) {
            alertError("Ups, ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    private void onBackClicked() {
        cambiarVista("/views/hello-view.fxml", "STELLA");
    }

    @FXML
    private void onForgotClicked() {
        cambiarVista("/views/RecuperarContra.fxml", "STELLA - Recuperar contraseña");
    }

    // ===== Navegación reutilizable =====
    private void cambiarVista(String fxmlPath, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Muy importante: usar la misma factory global del orquestador
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            Stage stage = (Stage) correoField.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(tituloVentana);
            stage.centerOnScreen();

        } catch (Exception e) {
            alertError("No se pudo cargar " + fxmlPath + " : " + e.getMessage());
        }
    }

    private void alertError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
