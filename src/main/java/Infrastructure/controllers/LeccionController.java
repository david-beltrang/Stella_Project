package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.function.Function;

public class LeccionController {

    // Dependencias
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // FXML
    @FXML private Pane root;
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;
    @FXML private Button btnVolver, btnSiguiente;

    // Datos de la lección actual
    private LeccionResponse leccionActual;

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    public void setLeccionActual(LeccionResponse leccion) {
        this.leccionActual = leccion;
    }

    @FXML
    public void initialize() {
        // Aquí luego podrás llenar dinámicamente los Labels según el ID de la lección
    }

    // ===================== Navegación =====================

    @FXML
    private void navegarALeccionAnterior() {
        try {
            if (leccionActual == null || leccionActual.numeroOrden() <= 1) {
                uiHelper.showInfo("Inicio del curso", "Ya estás en la primera lección.");
                return;
            }

            int anterior = leccionActual.numeroOrden() - 1;
            navigator.goTo("/views/Leccion" + anterior + ".fxml", "Lección " + anterior, controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al retroceder", e.getMessage());
        }
    }

    @FXML
    private void navegarALeccionSiguiente() {
        try {
            if (leccionActual == null) {
                uiHelper.showError("Error", "No se ha cargado ninguna lección actual.");
                return;
            }

            int siguiente = leccionActual.numeroOrden() + 1;
            navigator.goTo("/views/Leccion" + siguiente + ".fxml", "Lección " + siguiente, controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al avanzar", e.getMessage());
        }
    }
}

