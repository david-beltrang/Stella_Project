package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Application.services.LeccionService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.function.Function;

public class LeccionController {

    // ===== DEPENDENCIAS =====
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private final LeccionService leccionService;
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Pane root;

    // ===== ESTADO =====
    private LeccionResponse leccionActual;
    private int numeroActual = 1;
    private int cursoId = -1;
    private int seccionOrden = -1;

    // ===== CONSTRUCTOR =====
    public LeccionController(LeccionService leccionService) {
        this.leccionService = leccionService;
    }

    public LeccionController() {
        this.leccionService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== SETTERS =====
    public void setNumeroActual(int n) { this.numeroActual = Math.max(1, n); }
    public void setLeccionActual(LeccionResponse l) { this.leccionActual = l; }
    public void setCursoYSeccion(int cursoId, int seccionOrden) {
        this.cursoId = cursoId;
        this.seccionOrden = seccionOrden;
    }


    @FXML
    public void initialize() {
        if (leccionActual != null) {
            renderLeccion();
        } else {
            System.out.println("[INFO] LeccionController inicializado sin lección actual.");
        }
    }

    // ===== mostrar contenido =====
    private void renderLeccion() {
        if (root == null || leccionActual == null) return;

        int numero = leccionActual.numeroOrden();
        String titulo = leccionActual.titulo();
        String contenido = leccionActual.contenido();
        String tipo = leccionActual.tipoContenido();
        String urlVideo = leccionActual.url_video();

        setText("TituloSeccion", "LECCIÓN " + numero);
        setText("NombreLeccion" + numero, titulo);

        switch (numero) {
            case 1 -> {
                setText("DescripcionLeccion1", contenido);
                setText("CaracteristicasTituloLeccion1", "Características");
                setText("Caracteristicas1", "• " + contenido);
                setText("AmbitosdeUsoTtitulo1", "Ámbitos de uso");
                setText("Ambitos1", "Se usa cuando el curso lo requiera.");
            }
            case 2 -> {
                setText("DescripcionLeccion2", contenido);
                setText("TitulodelVideo2", "Video / recurso");
                if (urlVideo != null && !urlVideo.isBlank()) {
                    setText("LinkYoutube2", urlVideo);
                }
            }
            case 3 -> {
                setText("TextoPreguntaQuiz3", contenido);
                setText("Opciona3", "Opción A");
                setText("Opcionb3", "Opción B");
                setText("Opcionc3", "Opción C");
                setText("Opciond3", "Opción D");
            }
            case 4 -> {
                setText("DescripcionLeccion4", contenido);
                setText("Codigo4", "int main() {\n    // ejemplo\n}");
                setText("TituloComponentesEscenciales4", "Componentes esenciales");
                setText("ComponentesEscenciales4", "• declaración\n• entrada/salida\n• compilación");
            }
            case 5 -> {
                setText("DescripcionLeccion5", contenido);
                setText("TituloInstrucciones5", "Instrucciones");
                setText("Instrucciones5", "1. Lee el enunciado\n2. Escribe el código\n3. Ejecuta.");
                setText("Codigo5", "cout << \"Hola\";");
                setText("ResultadoEsperado5", "Salida esperada en consola");
            }
            default -> setText("DescripcionLeccion1", contenido);
        }
    }

    private void setText(String id, String text) {
        if (root == null) return;
        Node n = root.lookup("#" + id);
        if (n instanceof Label lbl) lbl.setText(text != null ? text : "");
    }

    // ===== NAVEGACIÓN =====
    @FXML
    private void navegarALeccionAnterior(ActionEvent e) {
        int anterior = numeroActual - 1;
        if (anterior < 1) {
            uiHelper.showInfo("Inicio del curso", "Ya estás en la primera lección.");
            return;
        }
        irA(anterior, (Node) e.getSource());
    }

    @FXML
    private void navegarALeccionSiguiente(ActionEvent e) {
        int siguiente = numeroActual + 1;
        irA(siguiente, (Node) e.getSource());
    }

    private void irA(int numeroDestino, Node source) {
        try {
            // obtenemos la nueva lección directamente al declarar la variable → efectivamente final
            final LeccionResponse nueva = (leccionService != null && cursoId > 0 && seccionOrden > 0)
                    ? leccionService.obtenerLeccionPorCursoYOrden(cursoId, seccionOrden, numeroDestino)
                    : null;

            if (nueva == null) {
                uiHelper.showInfo("Fin del curso", "No hay más lecciones disponibles.");
                return;
            }

            String ruta = "/views/Leccion" + numeroDestino + ".fxml";
            navigator.goToWithInit(
                    ruta,
                    "Lección " + numeroDestino,
                    controllerFactory,
                    (source != null ? source : root),
                    (LeccionController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.setNumeroActual(numeroDestino);
                        c.setCursoYSeccion(cursoId, seccionOrden);
                        c.setLeccionActual(nueva);
                    }
            );

        } catch (Exception ex) {
            uiHelper.showError("Error al cambiar de lección", ex.getMessage());
            ex.printStackTrace();
        }
    }
}
