package Infrastructure.controllers;

import Infrastructure.ui.NavigationManager;
import Infrastructure.ui.UIFeedbackHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.function.Function;

public class HelloController {

    @FXML private Button loginButton;
    @FXML private Button registroButton;

    // ====== Dependencias de UI comunes ======
    private final NavigationManager navigator = new NavigationManager();
    private final UIFeedbackHelper uiHelper = new UIFeedbackHelper();

    // ====== Factory global para la inyección de controladores ======
    private Function<Class<?>, Object> controllerFactory;

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ====== EVENTOS DE BOTONES ======
    @FXML
    private void goToLogin() {
        try {
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, loginButton);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir login", e.getMessage());
        }
    }

    @FXML
    private void goToRegistro() {
        try {
            navigator.goTo("/views/Registro.fxml", "STELLA - Registro", controllerFactory, registroButton);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir registro", e.getMessage());
        }
    }
}

