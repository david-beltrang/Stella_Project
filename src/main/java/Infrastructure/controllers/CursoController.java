package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.SeccionesService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
    @FXML private Pane root;              // inyectado al cargar PlantillaCurso.fxml
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

    private void cargarSeccionesYLecciones() {
        try {
            if (root == null) { System.err.println("[WARN] root todavía es null, no pinto."); return; }
            if (seccionesService == null) { System.err.println("[WARN] seccionesService es null."); return; }
            if (cursoActualId <= 0) { System.err.println("[WARN] cursoActualId no seteado."); return; }

            List<SeccionResponse> secciones = seccionesService.ListarSeccionesConLecciones(cursoActualId);
            System.out.println("📘 Secciones encontradas: " + secciones.size());

            for (SeccionResponse seccion : secciones) {
                for (LeccionResponse leccion : seccion.lecciones()) {

                    // buscar botón por ambas convenciones: "Leccion1.1" y "Leccion1_1"
                    String idPunto = "Leccion" + seccion.numeroOrden() + "." + leccion.numeroOrden();
                    String idGuion = "Leccion" + seccion.numeroOrden() + "_" + leccion.numeroOrden();

                    Button btn = (Button) root.lookup("#" + idPunto);
                    if (btn == null) btn = (Button) root.lookup("#" + idGuion);

                    if (btn == null) {
                        System.out.println("   ❌ No se encontró el botón #" + idPunto + " ni #" + idGuion);
                        continue;
                    }

                    btn.setText(leccion.titulo());
                    btn.setDisable(false);
                    btn.setOpacity(1.0);

                    final LeccionResponse lec = leccion;
                    btn.setOnAction(e -> abrirLeccion(lec, (Node) e.getSource()));
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

    /** Navegación simple por número de lección (sin pasar DTOs ni servicios al controller). */
    private void abrirLeccion(LeccionResponse leccion, Node source) {
        try {
            int numero = (leccion != null) ? leccion.numeroOrden() : 1;
            String ruta = "/views/Leccion" + numero + ".fxml";

            navigator.goToWithInit(
                    ruta,
                    "Lección " + numero,
                    controllerFactory,
                    (source != null ? source : root),
                    (LeccionController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.setNumeroActual(numero);   // <-- solo el número para navegar
                    }
            );

        } catch (Exception e) {
            uiHelper.showError("Error al abrir lección", e.getMessage());
            e.printStackTrace();
        }
    }
}
