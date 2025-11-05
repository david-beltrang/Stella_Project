package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.leccion.LeccionResponse;
import Application.services.LeccionService;
import Application.services.PomodoroTimer;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.util.function.Function;

public class LeccionController {

    // ===== DEPENDENCIAS =====
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private final LeccionService leccionService;
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Pane root;
    @FXML private WebView videoWebView;
    @FXML private Label lblTiempoPomodoro; // ⏱ Label del Pomodoro (agrega en cada FXML)

    // ===== ESTADO =====
    private LeccionResponse leccionActual;
    private int numeroActual = 1;
    private int cursoId = -1;
    private int seccionOrden = -1;

    // ===== CONSTRUCTORES =====
    public LeccionController(LeccionService leccionService) {
        this.leccionService = leccionService;
    }

    public LeccionController() {
        this.leccionService = null;
    }

    // ===== SETTERS =====
    public void setControllerFactory(Function<Class<?>, Object> factory) { this.controllerFactory = factory; }
    public void setNumeroActual(int n) { this.numeroActual = Math.max(1, n); }
    public void setCursoYSeccion(int cursoId, int seccionOrden) { this.cursoId = cursoId; this.seccionOrden = seccionOrden; }

    public void setLeccionActual(LeccionResponse l) {
        this.leccionActual = l;
        if (root != null) renderLeccion();
    }

    @FXML
    public void initialize() {
        if (leccionActual != null) {
            renderLeccion();
        } else {
            System.out.println("[INFO] initialize(): sin lección actual todavía.");
        }
        // ⏱ Enlazar el label al Pomodoro global
        setupPomodoroBinding();
    }

    // ===== Pomodoro HUD =====
    private void setupPomodoroBinding() {
        try {
            if (lblTiempoPomodoro == null) return; // si el FXML no tiene el label, no hacemos nada
            PomodoroTimer timer = AppServices.getPomodoroTimer();
            if (timer == null) return;

            // Vincula el texto del label al secondsLeft del timer, formateado mm:ss
            lblTiempoPomodoro.textProperty().unbind();
            lblTiempoPomodoro.textProperty().bind(
                    Bindings.createStringBinding(
                            () -> formatMMSS(timer.secondsLeftProperty().get()),
                            timer.secondsLeftProperty()
                    )
            );

            // Si venimos de otra pantalla y estaba pausado, reanudar (sin reiniciar)
            if (timer.secondsLeftProperty().get() > 0) {
                timer.start();
            }
        } catch (Exception e) {
            System.err.println("❌ Error al configurar Pomodoro HUD: " + e.getMessage());
        }
    }

    private String formatMMSS(int total) {
        if (total < 0) total = 0;
        int mm = total / 60;
        int ss = total % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    // ===== RENDERIZAR =====
    private void renderLeccion() {
        if (root == null) { System.err.println("[WARN] root es null al intentar renderizar."); return; }
        if (leccionActual == null) { System.err.println("[WARN] No hay lección actual para renderizar."); return; }

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

                if (urlVideo != null && !urlVideo.isBlank() && videoWebView != null) {
                    try {
                        String html = """
                                <html>
                                  <head><meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
                                  <body style="margin:0; background-color:#080736; display:flex; justify-content:center; align-items:center; height:100vh; color:white; font-family:Arial;">
                                    <a href="%s" style="color:white; font-size:30px; text-decoration:none; padding:15px 25px; border:2px solid white; border-radius:12px;">
                                      ▶ Ver video en YouTube
                                    </a>
                                  </body>
                                </html>
                                """.formatted(urlVideo);
                        videoWebView.getEngine().loadContent(html);
                        System.out.println("🎬 Enlace mostrado correctamente: " + urlVideo);
                    } catch (Exception ex) {
                        System.err.println("❌ Error al cargar enlace de video: " + ex.getMessage());
                        uiHelper.showError("Error al mostrar video", "No se pudo generar el enlace al video.");
                    }
                } else {
                    System.out.println("No se encontró URL de video válida o el WebView no está disponible.");
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
                // Mostrar breve introducción
                setText("DescripcionLeccion5", "¡Hora de evaluar lo aprendido!");
                setText("TituloInstrucciones5", "Evaluación final");
                setText("Instrucciones5", "Responde las siguientes preguntas para completar el módulo.");

                // Agregar un pequeño delay visual antes de redirigir (opcional)
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(500); // pequeña pausa para que se vea el texto anterior
                        navigator.goTo(
                                "/views/Leccion5.fxml",
                                "Evaluación final",
                                controllerFactory,
                                root
                        );
                    } catch (Exception e) {
                        uiHelper.showError("Error al abrir el quiz", e.getMessage());
                    }
                });
            }
            default -> setText("DescripcionLeccion1", contenido);
        }

        System.out.println(" Renderizada lección " + numero + ": " + titulo);
    }

    // ===== UTILIDAD =====
    private void setText(String id, String text) {
        if (root == null) return;
        Node n = root.lookup("#" + id);
        if (n instanceof Label lbl) {
            lbl.setText(text != null ? text : "");
        } else {
            System.out.println("⚠ No se encontró label con id #" + id);
        }
    }

    // ===== NAVEGACIÓN ENTRE LECCIONES =====
    @FXML
    private void navegarALeccionAnterior(javafx.event.ActionEvent e) {
        int anterior = numeroActual - 1;
        if (anterior < 1) {
            uiHelper.showInfo("Inicio del curso", "Ya estás en la primera lección.");
            return;
        }
        irA(anterior, (Node) e.getSource());
    }

    @FXML
    private void navegarALeccionSiguiente(javafx.event.ActionEvent e) {
        int siguiente = numeroActual + 1;
        irA(siguiente, (Node) e.getSource());
    }

    private void irA(int numeroDestino, Node source) {
        try {
            if (cursoId <= 0 || seccionOrden <= 0) {
                uiHelper.showError("Error de contexto", "No se definieron curso o sección para esta lección.");
                return;
            }

            LeccionResponse nueva = leccionService != null
                    ? leccionService.obtenerLeccionPorCursoYOrden(cursoId, seccionOrden, numeroDestino)
                    : null;

            if (nueva == null) {
                uiHelper.showInfo("Fin de la sección", "No hay más lecciones en esta sección.");
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
