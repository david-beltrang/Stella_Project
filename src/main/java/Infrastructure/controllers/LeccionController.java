package Infrastructure.controllers;

import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.IntFunction;

public class LeccionController {

    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    private Function<Class<?>, Object> controllerFactory;

    @FXML private Pane root;

    // 👉 navegación por número (como ya lo tenías funcionando)
    private int numeroActual = 1;
    public void setNumeroActual(int n) { this.numeroActual = Math.max(1, n); }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // 👉 opcional: DTO de la lección para pintar contenido
    private Object leccionActual;
    public void setLeccionActual(Object leccion) {
        this.leccionActual = leccion;
        if (root != null) renderLeccion();
    }

    // 👉 opcional: cómo traer una lección por número cuando navego
    private IntFunction<Object> fetchLeccionByOrden;
    public void setFetchLeccionByOrden(IntFunction<Object> f) { this.fetchLeccionByOrden = f; }

    @FXML
    public void initialize() {
        // Título general
        setText("TituloSeccion", "LECCIÓN " + numeroActual);
        setText("NombreLeccion" + numeroActual, "Lección " + numeroActual);

        // Si aún no me pasaron DTO pero me dieron fetcher, lo pido y pinto
        if (leccionActual == null && fetchLeccionByOrden != null) {
            leccionActual = fetchLeccionByOrden.apply(numeroActual);
        }
        if (leccionActual != null) {
            renderLeccion();
        }
    }

    // ======= NAV: igual que tenías, pero inyectando datos a la siguiente =======
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
        String ruta = "/views/Leccion" + numeroDestino + ".fxml";
        navigator.goToWithInit(
                ruta,
                "Lección " + numeroDestino,
                controllerFactory,
                (source != null ? source : root),
                (LeccionController c) -> {
                    c.setControllerFactory(controllerFactory);
                    c.setNumeroActual(numeroDestino);
                    c.setFetchLeccionByOrden(fetchLeccionByOrden);    // re-usa el fetcher
                    if (fetchLeccionByOrden != null) {
                        Object dto = fetchLeccionByOrden.apply(numeroDestino);
                        if (dto != null) c.setLeccionActual(dto);      // pinta contenido
                    } else if (leccionActual != null) {
                        // sin fetcher: al menos ajusta los textos genéricos
                        c.setLeccionActual(leccionActual);
                    }
                }
        );
    }

    // ======= PINTAR CONTENIDO (idéntico a lo que ya usabas por reflexión) =======
    private void renderLeccion() {
        if (root == null || leccionActual == null) return;

        int numero = getInt(leccionActual, "numeroOrden", "getNumeroOrden");
        String titulo = getString(leccionActual, "titulo", "getTitulo");
        String contenido = getString(leccionActual, "contenido", "getContenido");
        String urlVideo = getString(leccionActual, "urlVideo", "getUrlVideo");

        if (numero <= 0) numero = numeroActual;
        if (titulo == null) titulo = "Lección " + numero;
        if (contenido == null) contenido = "";

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
}
