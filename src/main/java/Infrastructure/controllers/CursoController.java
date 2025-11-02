package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.SeccionesService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;

public class CursoController {

    // ===== deps =====
    private final SeccionesService seccionesService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Pane root;              // se inyecta SOLO cuando se carga PlantillaCurso.fxml
    @FXML private Label NombreCurso;

    // ===== estado =====
    private int cursoActualId = -1;
    private String cursoTitulo = "CURSO DESCONOCIDO";

    // ctor DI
    public CursoController(SeccionesService seccionesService) {
        this.seccionesService = seccionesService;
    }

    // ctor vacío (FXML)
    public CursoController() {
        this.seccionesService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // lo llama PrincipalController ANTES de cargar la vista
    public void setCursoActual(int id, String titulo) {
        this.cursoActualId = id;
        this.cursoTitulo = titulo;

        // si YA está la vista cargada, pinto; si no, espero al initialize()
        if (root != null) {
            cargarSeccionesYLecciones();
        }
    }

    @FXML
    public void initialize() {
        // aquí ya hay root
        if (NombreCurso != null) {
            NombreCurso.setText("CURSO " + cursoTitulo.toUpperCase());
        }

        // si ya nos habían pasado el id antes, ahora sí pintamos
        if (seccionesService != null && cursoActualId > 0) {
            cargarSeccionesYLecciones();
        }
    }

    private void cargarSeccionesYLecciones() {
        try {
            if (root == null) {
                // seguridad extra
                System.err.println("[WARN] root todavía es null, no pinto.");
                return;
            }
            if (seccionesService == null) {
                System.err.println("[WARN] seccionesService es null.");
                return;
            }
            if (cursoActualId <= 0) {
                System.err.println("[WARN] cursoActualId no seteado.");
                return;
            }

            List<SeccionResponse> secciones = seccionesService.ListarSeccionesConLecciones(cursoActualId);
            System.out.println("📘 Secciones encontradas: " + secciones.size());

            for (SeccionResponse seccion : secciones) {
                for (LeccionResponse leccion : seccion.lecciones()) {

                    // en tu FXML: fx:id="Leccion1.1" → punto
                    String fxId = "Leccion" + seccion.numeroOrden() + "_" + leccion.numeroOrden();
                    Button btn = (Button) root.lookup("#" + fxId);

                    if (btn == null) {
                        System.out.println("   ❌ No se encontró el botón #" + fxId);
                        continue;
                    }

                    btn.setText(leccion.titulo());
                    btn.setDisable(false);
                    btn.setOpacity(1.0);
                    btn.setOnAction(e -> abrirLeccion(leccion));
                }
            }

            if (NombreCurso != null) {
                NombreCurso.setText("CURSO " + cursoTitulo.toUpperCase());
            }

        } catch (Exception e) {
            uiHelper.showError("Error al cargar curso", e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirLeccion(LeccionResponse leccion) {
        try {
            String ruta = "/views/Leccion" + leccion.numeroOrden() + ".fxml";

            LeccionController lecCtrl = (LeccionController) controllerFactory.apply(LeccionController.class);
            lecCtrl.setLeccionActual(leccion);

            navigator.goTo(ruta, leccion.titulo(), controllerFactory, root);

        } catch (Exception e) {
            uiHelper.showError("Error al abrir lección", e.getMessage());
            e.printStackTrace();
        }
    }
}
