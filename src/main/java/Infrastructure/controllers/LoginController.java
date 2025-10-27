package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Infrastructure.ui.NavigationManager;
import Infrastructure.ui.UIFeedbackHelper;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Function;

public class LoginController {

    // ===== Dependencias de negocio =====
    private final LoginService service;

    // ===== UI Helpers =====
    private final UIFeedbackHelper uiHelper = new UIFeedbackHelper();
    private final NavigationManager navigator = new NavigationManager();

    // ===== Navegación (inyectada) =====
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

    // ===== Referencias a la vista (inyectadas por FXML) =====
    @FXML private TextField correoField;
    @FXML private PasswordField passwordField;

    // ===== Lógica principal =====
    @FXML
    private void onLoginClicked() {
        String correo = correoField.getText();
        String pass   = passwordField.getText();

        if (correo == null || correo.isBlank()) {
            uiHelper.showError("Error de validación", "Falta el correo");
            return;
        }
        if (pass == null || pass.isBlank()) {
            uiHelper.showError("Error de validación", "Falta la contraseña");
            return;
        }

        try {
            if (service == null) {
                throw new IllegalStateException("LoginService no inicializado correctamente.");
            }

            UsuarioResponse usuario = service.login(new LoginRequest(correo, pass));

            // AppServices.setUsuarioActual(usuario); // si quieres guardar sesión

            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, correoField);

        } catch (IllegalArgumentException ex) {
            uiHelper.showError("Error de login", ex.getMessage());
        } catch (Exception ex) {
            uiHelper.showError("Error inesperado", ex.getMessage());
        }
    }

    @FXML
    private void onBackClicked() {
        navigator.goTo("/views/hello-view.fxml", "STELLA", controllerFactory, correoField);
    }

    @FXML
    private void onForgotClicked() {
        navigator.goTo("/views/RecuperarContra.fxml", "STELLA - Recuperar contraseña", controllerFactory, correoField);
    }
}

