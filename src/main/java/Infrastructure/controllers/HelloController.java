package Infrastructure.controllers;

import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.function.Function;

public class HelloController {

    @FXML private Button loginButton;
    @FXML private Button registroButton;

    // Dependencias de UI
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // Factory global para la inyección de controladores
    // Osea esto permite mantener las mismas instancias entre vistas al cambiar de escena
    private Function<Class<?>, Object> controllerFactory;

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    //  ACCIONES DE BOTONES
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

