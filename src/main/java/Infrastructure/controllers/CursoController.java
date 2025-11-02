package Infrastructure.controllers;

import Application.dtos.seccion.SeccionResponse;
import Application.dtos.leccion.LeccionResponse;
import Application.services.SeccionesService;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;

/**
 * Controlador que maneja la plantilla del curso (PlantillaCurso.fxml).
 * Carga las secciones y lecciones dinámicamente desde el servicio SeccionesService,
 * bloquea las lecciones no completadas y maneja la navegación hacia las pantallas de cada lección.
 */
public class CursoController {

    // ======= Dependencias =======
    private final SeccionesService seccionesService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ======= FXML =======
    @FXML private Pane root;
    @FXML private Label NombreCurso;

    public CursoController(SeccionesService seccionesService) {
        this.seccionesService = seccionesService;
    }

    public CursoController() {
        this.seccionesService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ======= Inicialización =======
    @FXML
    public void initialize() {
        if (seccionesService == null) {
            uiHelper.showError("Error", "El servicio de secciones no está disponible.");
            return;
        }

        try {
            int cursoId = 1; // Aquí podrías obtenerlo dinámicamente según el curso actual
            List<SeccionResponse> secciones = seccionesService.ListarSeccionesConLecciones(cursoId);

            for (SeccionResponse seccion : secciones) {
                for (LeccionResponse leccion : seccion.lecciones()) {
                    // ID visual esperado: Leccion<numeroSeccion>.<numeroLeccion>
                    String fxId = "Leccion" + seccion.numeroOrden() + "." + leccion.numeroOrden();

                    Button boton = (Button) root.lookup("#" + fxId);
                    if (boton != null) {
                        boton.setText(leccion.titulo());
                        boton.setUserData(leccion.id());

                        // Bloquea si la lección no está completada
                        if (leccion.tipoContenido().equalsIgnoreCase("PENDIENTE")) {
                            boton.setDisable(true);
                            boton.setOpacity(0.6);
                        } else {
                            boton.setDisable(false);
                            boton.setOpacity(1.0);
                            boton.setOnAction(e -> abrirLeccion(leccion));
                        }
                    }
                }
            }
        } catch (Exception e) {
            uiHelper.showError("Error al cargar curso", e.getMessage());
        }
    }

    // ======= Navegación =======
    private void abrirLeccion(LeccionResponse leccion) {
        try {
            String ruta = "/views/leccion" + leccion.numeroOrden() + ".fxml";
            navigator.goTo(ruta, leccion.titulo(), controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir lección", e.getMessage());
        }
    }

    // ======= Barra inferior =======
    @FXML private void goHome()        { navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, root); }
    @FXML private void goForum()       { uiHelper.showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements(){ uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()     { uiHelper.showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }
}
