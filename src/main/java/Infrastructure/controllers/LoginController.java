package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Infrastructure.ui.Navegacion;
import Infrastructure.ui.AyudaUI;
import Domain.strategies.ValidationStrategy;
import Domain.strategies.EmailValidationStrategy;
import Domain.strategies.PasswordValidationStrategy;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de la vista Login.fxml
 * Maneja los eventos de inicio de sesión, recuperación de contraseña y
 * navegación hacia otras pantallas.
 */
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // ===== Dependencias de negocio =====
    private final LoginService service;

    // ===== Strategies (Strategy Pattern) =====
    private final ValidationStrategy emailValidator = new EmailValidationStrategy();
    private final ValidationStrategy passwordValidator = new PasswordValidationStrategy();

    // ===== Dependencias de interfaz =====
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navegacion navigator = new Navegacion();

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
    @FXML
    private TextField correoField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button backButton;
    @FXML
    private Button forgotPasswordButton;

    @FXML
    public void initialize() {
        // Clear previous login credentials (no hay ViewModel, solo limpia los campos)
        correoField.clear();
        passwordField.clear();
    }

    // ===== Evento principal =====
    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass = passwordField.getText();

        try {
            // 1️⃣ Validación (Strategy Pattern)
            emailValidator.validate(correo);
            passwordValidator.validate(pass);

            // 2️⃣ Autenticación (delegado al servicio - Controller Pattern)
            UsuarioResponse usuario = service.login(new LoginRequest(correo, pass));

            // 3️⃣ Guardar el usuario globalmente (delegado al servicio de sesión)
            AppServices.setUsuarioActual(usuario);

            // 4️⃣ Navegación delegada a Navigacion (Separation of Concerns)
            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, correoField);

            // 5️⃣ Mensaje personalizado
            uiHelper.showInfo(
                    "Bienvenido " + usuario.nombre(),
                    "Has iniciado sesión correctamente. Tu ID es: " + usuario.id());

        } catch (IllegalArgumentException ex) {
            logger.warn("Error de validación en login para correo: {}", correo);
            uiHelper.showError("Error de validación", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error inesperado durante el login", ex);
            uiHelper.showError("Error inesperado", ex.getMessage());
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
    @FXML
    private TextField recoverEmailField;
    @FXML
    private Label recoveryMessage;
    @FXML
    private Button sendRecoveryButton;
    @FXML
    private Button backToLoginButton;

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