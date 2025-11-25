package Infrastructure.controllers;

import Application.config.AppServices;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class ForoController {
    private static final Logger logger = LoggerFactory.getLogger(ForoController.class);

    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    @FXML
    private Button homeBtn2;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button forumBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private Button tiendaBtn;
    @FXML
    private Button chatBtn1;

    public ForoController() {
        // Constructor vacío para JavaFX
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        logger.debug("Iniciando ForoController...");
        // Aquí se puede agregar lógica para cargar posts del foro desde la BD
        logger.debug("Foro inicializado correctamente");
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        try {
            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al inicio desde foro", e);
            uiHelper.showError("Error al navegar al inicio", e.getMessage());
        }
    }

    @FXML
    private void goProfile() {
        try {
            navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al perfil desde foro", e);
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar a la tienda desde foro", e);
            uiHelper.showError("Error al navegar a la tienda", e.getMessage());
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            AppServices.cerrarSesion();
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);
            logger.info("Sesión cerrada correctamente desde foro");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión desde foro", e);
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
        }
    }
}
