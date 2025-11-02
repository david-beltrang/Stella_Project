package Infrastructure.controllers;

import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.lang.reflect.Method;
import java.util.function.Function;

public class LeccionController {

    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    @FXML private Pane root;
    @FXML private Button btnVolver;
    @FXML private Button btnSiguiente;

    // viene del CursoController
    private Object leccionActual;   // <- NO uso tu DTO, uso Object

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // lo llama CursoController antes de navegar
    public void setLeccionActual(Object leccion) {
        this.leccionActual = leccion;
        if (root != null) {
            renderLeccion();
        }
    }

    @FXML
    public void initialize() {
        if (leccionActual != null) {
            renderLeccion();
        } else {
            System.out.println("[LeccionController] initialize() sin leccionActual todavía");
        }
    }

    private void renderLeccion() {
        if (root == null || leccionActual == null) return;

        // ====== leer datos SIN asumir DTO ======
        int numero = getInt(leccionActual, "numeroOrden", "getNumeroOrden");
        String titulo = getString(leccionActual, "titulo", "getTitulo");
        String contenido = getString(leccionActual, "contenido", "getContenido");
        String tipo = getString(leccionActual, "tipoContenido", "getTipoContenido");
        String urlVideo = getString(leccionActual, "urlVideo", "getUrlVideo");

        if (numero <= 0) numero = 1;            // por si viene 0
        if (titulo == null) titulo = "Lección " + numero;
        if (contenido == null) contenido = "";

        // ====== título grande ======
        setText("TituloSeccion", "LECCIÓN " + numero);

        // ====== nombre lección (sin punto) ======
        setText("NombreLeccion" + numero, titulo);

        // ====== pintar según PLANTILLA (por número) ======
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
            default -> {
                // cualquier otra: muestra título y contenido
                setText("DescripcionLeccion1", contenido);
            }
        }
    }

    // ============ helpers UI ============

    private void setText(String id, String text) {
        if (root == null) return;
        Node n = root.lookup("#" + id);
        if (n instanceof Label lbl) {
            lbl.setText(text != null ? text : "");
        } else {
            System.out.println("[LeccionController] no encontré label #" + id);
        }
    }

    // ============ helpers REFLEXIÓN ============

    private String getString(Object target, String... names) {
        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v != null) return v.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private int getInt(Object target, String... names) {
        for (String name : names) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v instanceof Number num) return num.intValue();
            } catch (Exception ignored) {}
        }
        return 0;
    }

    // ============ navegación ============

    @FXML
    private void navegarALeccionAnterior() {
        if (leccionActual == null) {
            uiHelper.showInfo("Inicio del curso", "Ya estás en la primera lección.");
            return;
        }
        int actual = getInt(leccionActual, "numeroOrden", "getNumeroOrden");
        int anterior = actual - 1;
        if (anterior < 1) {
            uiHelper.showInfo("Inicio del curso", "Ya estás en la primera lección.");
            return;
        }
        navigator.goTo("/views/Leccion" + anterior + ".fxml",
                "Lección " + anterior,
                controllerFactory,
                root);
    }

    @FXML
    private void navegarALeccionSiguiente() {
        if (leccionActual == null) {
            uiHelper.showError("Error", "No se ha cargado ninguna lección actual.");
            return;
        }
        int actual = getInt(leccionActual, "numeroOrden", "getNumeroOrden");
        int siguiente = actual + 1;
        if (siguiente > 5) {
            uiHelper.showInfo("Fin", "No hay más lecciones en esta plantilla.");
            return;
        }
        navigator.goTo("/views/Leccion" + siguiente + ".fxml",
                "Lección " + siguiente,
                controllerFactory,
                root);
    }
}





