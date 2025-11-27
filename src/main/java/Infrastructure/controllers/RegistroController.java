package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.RegistroService;
import Infrastructure.ui.Navegacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.util.function.Function;

public class RegistroController {

    // ===== Dependencias de negocio =====
    private final RegistroService service;

    // ===== UI=====
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navegacion navigator = new Navegacion();

    // ===== Factory para navegación =====
    private Function<Class<?>, Object> controllerFactory;

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== Constructores =====
    public RegistroController(RegistroService service) {
        this.service = service;
    }

    public RegistroController() {
        this.service = AppServices.registroService();
    }

    // ===== Nodos FXML =====
    @FXML
    private TextField nombreField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField correoField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button backButton;
    @FXML
    private Button registerButton;

    // ===== Acciones de UI =====
    @FXML
    private void onRegistrarClicked(ActionEvent e) {
        String nombre = text(nombreField);
        String username = text(usernameField);
        String correo = text(correoField);
        String pass1 = text(passwordField);
        String pass2 = text(confirmPasswordField);

        if (!correo.contains("@")) {
            uiHelper.showError("Correo inválido", "El correo debe tener formato usuario@dominio.com");
            return;
        }
        if (nombre.isBlank() || username.isBlank() || correo.isBlank() || pass1.isBlank() || pass2.isBlank()) {
            uiHelper.showError("Campos incompletos", "Por favor completa todos los campos antes de continuar.");
            return;
        }

        if (!pass1.equals(pass2)) {
            uiHelper.showError("Contraseñas no coinciden", "Ambas contraseñas deben ser iguales.");
            return;
        }

        try {
            if (service == null) {
                throw new IllegalStateException(
                        "RegistroService no inicializado. Revisa la inyección de dependencias.");
            }

            RegistrarUsuarioRequest req = new RegistrarUsuarioRequest(
                    username, correo, nombre, pass1, "ESTUDIANTE");

            UsuarioResponse u = service.registrar(req);

            uiHelper.showInfo("Registro exitoso",
                    "Bienvenido " + u.nombre() + ".\nYa puedes iniciar sesión en STELLA.");

            // Navegar al login usando el origen del evento
            navigator.goTo(
                    "/views/Login.fxml",
                    "STELLA - Login",
                    controllerFactory,
                    (javafx.scene.Node) e.getSource());

        } catch (IllegalArgumentException ex) {
            uiHelper.showError("Error en el registro", ex.getMessage());
        } catch (Exception ex) {
            uiHelper.showError("Error inesperado", ex.getMessage());
        }
    }

    @FXML
    private void onVolverClicked(ActionEvent e) {
        navigator.goTo(
                "/views/hello-view.fxml",
                "STELLA",
                controllerFactory,
                (javafx.scene.Node) e.getSource());
    }

    // ===== Helpers internos =====
    private static String text(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }

}