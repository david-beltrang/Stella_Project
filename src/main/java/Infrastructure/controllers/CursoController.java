package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.SeccionesService;
import Application.services.LeccionService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursoController {
    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);

    // ===== DEPENDENCIAS =====
    private final SeccionesService seccionesService;
    private final LeccionService leccionService;
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Pane root;
    @FXML private Label NombreCurso;

    // ===== DATOS =====
    private int cursoActualId = -1;
    private String cursoTitulo = "CURSO DESCONOCIDO";

    // ===== CONSTRUCTOR =====
    public CursoController(SeccionesService seccionesService, LeccionService leccionService) {
        this.seccionesService = seccionesService;
        this.leccionService = leccionService;
    }

    public CursoController() {
        this.seccionesService = null;
        this.leccionService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== CONFIGURAR CURSO =====
    public void setCursoActual(int id, String titulo) {
        this.cursoActualId = id;
        this.cursoTitulo = titulo;

        if (root != null) {
            cargarSeccionesYLecciones();
        }
    }

    @FXML
    public void initialize() {
        if (NombreCurso != null) {
            NombreCurso.setText("CURSO " + cursoTitulo.toUpperCase());
        }
        if (seccionesService != null && cursoActualId > 0) {
            cargarSeccionesYLecciones();
        }
    }

    // ===== CARGAR SECCIONES Y LECCIONES =====
    private void cargarSeccionesYLecciones() {
        try {
            if (root == null) { logger.warn("root es null"); return; }
            if (seccionesService == null) { logger.warn("seccionesService es null"); return; }
            if (cursoActualId <= 0) { logger.warn("cursoActualId no seteado"); return; }

            List<SeccionResponse> secciones = seccionesService.ListarSeccionesConLecciones(cursoActualId);
            logger.debug("Secciones encontradas: {}", secciones.size());

            for (SeccionResponse seccion : secciones) {
                for (LeccionResponse leccion : seccion.lecciones()) {

                    // Buscar botón por convención (Leccion1.1 o Leccion1_1)
                    String idPunto = "Leccion" + seccion.numeroOrden() + "." + leccion.numeroOrden();
                    String idGuion = "Leccion" + seccion.numeroOrden() + "_" + leccion.numeroOrden();

                    Button btn = (Button) root.lookup("#" + idPunto);
                    if (btn == null) btn = (Button) root.lookup("#" + idGuion);

                    if (btn == null) {
                        logger.debug("No se encontró el botón #{} ni #{}", idPunto, idGuion);
                        continue;
                    }

                    btn.setText(leccion.titulo());
                    btn.setDisable(false);
                    btn.setOpacity(1.0);

                    // capturar el número de sección
                    final int seccionOrden = seccion.numeroOrden();
                    final LeccionResponse lec = leccion;

                    btn.setOnAction(e -> abrirLeccion(lec, seccionOrden, (Node) e.getSource()));
                }
            }

            if (NombreCurso != null) {
                NombreCurso.setText("CURSO " + cursoTitulo.toUpperCase());
            }

        } catch (Exception e) {
            logger.error("Error al cargar curso", e);
            uiHelper.showError("Error al cargar curso", e.getMessage());
        }
    }

    // ===== ABRIR LECCIÓN =====
    private void abrirLeccion(LeccionResponse leccion, int seccionOrden, Node source) {
        try {
            int numero = (leccion != null) ? leccion.numeroOrden() : 1;
            String ruta = "/views/Leccion" + numero + ".fxml";

            // Obtener contenido real desde BD
            LeccionResponse dto = leccionService.obtenerLeccionPorCursoYOrden(
                    cursoActualId,
                    seccionOrden,
                    numero
            );

            navigator.goToWithInit(
                    ruta,
                    "Lección " + numero,
                    controllerFactory,
                    (source != null ? source : root),
                    (LeccionController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.setNumeroActual(numero);
                        c.setCursoYSeccion(cursoActualId, seccionOrden);  // 🔹 esta línea hace toda la diferencia
                        c.setLeccionActual(dto);
                    }
            );

        } catch (Exception e) {
            logger.error("Error al abrir lección", e);
            uiHelper.showError("Error al abrir lección", e.getMessage());
        }
    }
}
