package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.control.Label;
import java.util.function.Function;

// Controlador de la vista Login.fxml
// Maneja los eventos de inicio de sesión, recuperación de contraseña y navegación hacia otras pantallas.
public class LoginController {

    // ===== Dependencias de negocio =====
    // Servicio que realiza la validación de credenciales y maneja la autenticación.
    private final LoginService service;

    // ===== Dependencias de interfaz (UI Helpers) =====
    // AyudaUI muestra mensajes visuales (errores, alertas, etc.)
    // Navigacion controla los cambios entre pantallas.
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navigacion navigator = new Navigacion();

    // ===== Factory global (inyectada) =====
    // Permite mantener las mismas instancias de controladores al cambiar de escena.
    private Function<Class<?>, Object> controllerFactory;

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== Constructores =====
    // Constructor principal (usado cuando ControllerControladores inyecta el servicio)
    public LoginController(LoginService service) {
        this.service = service;
    }

    // Constructor alternativo (para casos donde no se use inyección manual)
    public LoginController() {
        this.service = AppServices.service();
    }

    // ===== Referencias a los elementos de la vista =====
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;
    @FXML private Button backButton;
    @FXML private Button forgotPasswordButton;

    // ===== Eventos principales =====

    // Ejecuta el proceso de inicio de sesión cuando se presiona el botón "Ingresar".
    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        // Validación de campos vacíos
        if (correo == null || correo.isBlank()) {
            uiHelper.showError("Error de validación", "Debe ingresar un correo.");
            return;
        }
        if (pass == null || pass.isBlank()) {
            uiHelper.showError("Error de validación", "Debe ingresar una contraseña.");
            return;
        }

        try {
            if (service == null) {
                throw new IllegalStateException("LoginService no inicializado correctamente.");
            }

            // Autenticación mediante el servicio
            UsuarioResponse usuario = service.login(new LoginRequest(correo, pass));

            // Ejemplo: guardar el usuario actual en sesión (opcional)
            // AppServices.setUsuarioActual(usuario);

            // Redirección al menú principal si el login es exitoso
            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, correoField);

        } catch (IllegalArgumentException ex) {
            uiHelper.showError("Error de inicio de sesión", ex.getMessage());
        } catch (Exception ex) {
            uiHelper.showError("Error inesperado", ex.getMessage());
        }
    }

    // Regresa a la vista principal (pantalla de bienvenida)
    @FXML
    private void onBackClicked() {
        navigator.goTo("/views/hello-view.fxml", "STELLA", controllerFactory, backButton);
    }

    // Navega a la vista de recuperación de contraseña.
    @FXML
    private void onForgotClicked() {
        navigator.goTo("/views/RecuperarContra.fxml",
                "STELLA - Recuperar contraseña", controllerFactory, forgotPasswordButton);
    }

    @FXML
    private TextField recoverEmailField;
    @FXML
    private Label recoveryMessage;

    @FXML private Button sendRecoveryButton;
    @FXML private Button backToLoginButton;

    @FXML
    private void sendRecoveryEmail() {
        String email = recoverEmailField.getText();

        if (email == null || email.isBlank()) {
            uiHelper.showInfo("Campo vacío", "Por favor, ingresa un correo electrónico.");
            return;
        }

        // Espacio reservado para la lógica real de backend
        uiHelper.showInfo("Recuperación de contraseña",
                "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.");
    }

    @FXML
    private void goBackToLogin() {
        navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, recoverEmailField);
    }


}


