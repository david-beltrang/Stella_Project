package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.function.Function;

/**
 * Controlador de la vista Login.fxml
 * Maneja los eventos de inicio de sesión, recuperación de contraseña y navegación hacia otras pantallas.
 */
public class LoginController {

    // ===== Dependencias de negocio =====
    private final LoginService service;

    // ===== Dependencias de interfaz =====
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navigacion navigator = new Navigacion();

    // ===== Factory global =====
    private Function<Class<?>, Object> controllerFactory;

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== Constructores =====
    public LoginController(LoginService service) {
        this.service = service;
    }

    public LoginController() {
        this.service = AppServices.service();
    }

    // ===== FXML =====
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private Button forgotPasswordButton;

    // ===== Evento principal =====
    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        if (correo == null || correo.isBlank()) {
            uiHelper.showError("Error de validación", "Debe ingresar un correo.");
            return;
        }
        if (pass == null || pass.isBlank()) {
            uiHelper.showError("Error de validación", "Debe ingresar una contraseña.");
            return;
        }

        try {
            // 1️⃣ Autenticación
            UsuarioResponse usuario = service.login(new LoginRequest(correo, pass));

            // 2️⃣ Guardar el usuario globalmente
            AppServices.setUsuarioActual(usuario);

            // 3️⃣ Cambiar directamente de pantalla (sin duplicar controlador)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Principal.fxml"));
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) correoField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("STELLA - Principal");
            stage.centerOnScreen();

            // 4️⃣ Mensaje personalizado
            uiHelper.showInfo(
                    "Bienvenido " + usuario.nombre(),
                    "Has iniciado sesión correctamente. Tu ID es: " + usuario.id()
            );

        } catch (IllegalArgumentException ex) {
            uiHelper.showError("Error de inicio de sesión", ex.getMessage());
        } catch (Exception ex) {
            uiHelper.showError("Error inesperado", ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ===== Otros eventos =====
    @FXML
    private void onBackClicked() {
        navigator.goTo("/views/hello-view.fxml", "STELLA", controllerFactory, backButton);
    }

    @FXML
    private void onForgotClicked() {
        navigator.goTo("/views/RecuperarContra.fxml",
                "STELLA - Recuperar contraseña", controllerFactory, forgotPasswordButton);
    }

    // ===== Recuperación =====
    @FXML private TextField recoverEmailField;
    @FXML private Label recoveryMessage;
    @FXML private Button sendRecoveryButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void sendRecoveryEmail() {
        String email = recoverEmailField.getText();

        if (email == null || email.isBlank()) {
            uiHelper.showInfo("Campo vacío", "Por favor, ingresa un correo electrónico.");
            return;
        }

        uiHelper.showInfo("Recuperación de contraseña",
                "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.");
    }

    @FXML
    private void goBackToLogin() {
        navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, recoverEmailField);
    }
}
