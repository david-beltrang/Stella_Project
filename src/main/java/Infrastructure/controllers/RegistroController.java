package Infrastructure.controllers;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.RegistroService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.Function;

public class RegistroController {

    // ===== Dependencia de negocio =====
    private final RegistroService service;

    // ===== Factory global de controladores, para navegación =====
    private Function<Class<?>, Object> controllerFactory;

    // Setter para que el orquestador (ControllerControladores) inyecte la factory
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== Constructores =====

    // Constructor principal con DI (lo usará ControllerControladores)
    public RegistroController(RegistroService service) {
        this.service = service;
    }

    // Constructor vacío opcional (solo por si el FXMLLoader carga esto fuera de la factory;
    // si no quieres fallback, lo puedes eliminar)
    public RegistroController() {
        this.service = null;
    }

    // ===== Nodos FXML =====
    @FXML private TextField nombreField;
    @FXML private TextField usernameField;
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // ===== Acciones de UI =====
    @FXML
    private void onRegistrarClicked() {
        String nombre   = text(nombreField);
        String username = text(usernameField);
        String correo   = text(correoField);
        String pass1    = text(passwordField);
        String pass2    = text(confirmPasswordField);

        // Validaciones de UI (esto sí es responsabilidad del controlador)
        if (nombre.isBlank() || username.isBlank() || correo.isBlank() || pass1.isBlank() || pass2.isBlank()) {
            alert(Alert.AlertType.WARNING, "Completa todos los campos.");
            return;
        }
        if (!pass1.equals(pass2)) {
            alert(Alert.AlertType.WARNING, "Las contraseñas no coinciden.");
            return;
        }

        try {
            if (service == null) {
                throw new IllegalStateException(
                        "RegistroService no inicializado. Asegúrate de inyectarlo vía ControllerControladores."
                );
            }

            // Construimos el request (DTO) para el caso de uso
            RegistrarUsuarioRequest req = new RegistrarUsuarioRequest(
                    username,
                    correo,
                    nombre,
                    pass1,
                    "ESTUDIANTE" // <- tu tipo por defecto
            );

            UsuarioResponse u = service.registrar(req);

            alert(Alert.AlertType.INFORMATION,
                    " Registro exitoso: " + u.nombre() + "\nYa puedes iniciar sesión.");

            // Luego de registrar, mandamos al login
            cambiarVista("/views/Login.fxml", "STELLA - Login");

        } catch (IllegalArgumentException ex) {
            alert(Alert.AlertType.ERROR, "No se pudo registrar: " + ex.getMessage());
        } catch (Exception ex) {
            alert(Alert.AlertType.ERROR, "Error inesperado: " + ex.getMessage());
        }
    }

    @FXML
    private void onVolverClicked() {
        cambiarVista("/views/hello-view.fxml", "STELLA");
    }

    // ===== Navegación usando la misma factory global =====
    private void cambiarVista(String fxmlPath, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // MUY IMPORTANTE: usamos la misma factory global que viene del orquestador
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            Stage stage = (Stage) nombreField.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(tituloVentana);
            stage.centerOnScreen();

        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "No pude cargar " + fxmlPath + ": " + e.getMessage());
        }
    }

    // ===== Helpers internos del controlador =====
    private static String text(TextField tf) {
        return tf.getText() == null ? "" : tf.getText().trim();
    }

    private static void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}
