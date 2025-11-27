package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.leccion.LeccionResponse;
import Application.services.LeccionService;
import Application.services.EjercicioService;
import Application.services.PomodoroTimer;
import Application.services.UsuarioStatsService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;

import java.util.function.Function;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeccionController {
    private static final Logger logger = LoggerFactory.getLogger(LeccionController.class);

    // ===== DEPENDENCIAS =====
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private final LeccionService leccionService;
    private final EjercicioService ejercicioService;
    private final UsuarioStatsService usuarioStatsService;
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML
    private Pane root;
    @FXML
    private Label timerLabel;
    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;

    // ===== ESTADO =====
    private LeccionResponse leccionActual;
    private int numeroActual = 1;
    private int cursoId = -1;
    private int seccionOrden = -1;

    // ===== CONSTRUCTORES =====
    public LeccionController(LeccionService leccionService, EjercicioService ejercicioService,
            UsuarioStatsService usuarioStatsService) {
        this.leccionService = leccionService;
        this.ejercicioService = ejercicioService;
        this.usuarioStatsService = usuarioStatsService;
    }

    public LeccionController() {
        this.leccionService = null; // Se podría inyectar desde AppServices si existiera getter
        this.ejercicioService = null;
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
    }

    // ===== SETTERS =====
    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    public void setNumeroActual(int n) {
        this.numeroActual = Math.max(1, n);
    }

    public void setCursoYSeccion(int cursoId, int seccionOrden) {
        this.cursoId = cursoId;
        this.seccionOrden = seccionOrden;
    }

    public void setLeccionActual(LeccionResponse l) {
        this.leccionActual = l;
        if (root != null)
            renderLeccion();
    }

    @FXML
    public void initialize() {
        if (leccionActual != null) {
            renderLeccion();
        } else {
            logger.debug("initialize(): sin lección actual todavía");
        }
        // ⏱ Enlazar el label al Pomodoro global
        setupPomodoroBinding();
        cargarStats();
    }

    private void cargarStats() {
        if (usuarioStatsService == null)
            return;
        try {
            if (pescaditosLabel != null) {
                pescaditosLabel.setText(String.valueOf(usuarioStatsService.obtenerPescaditos()));
            }
            if (rachaLabel != null) {
                rachaLabel.setText(String.valueOf(usuarioStatsService.obtenerRachaDias()));
            }
        } catch (Exception e) {
            logger.error("Error cargando stats", e);
        }
    }

    // ===== Pomodoro HUD =====
    private void setupPomodoroBinding() {
        try {
            if (timerLabel == null)
                return; // si el FXML no tiene el label, no hacemos nada
            PomodoroTimer timer = AppServices.getPomodoroTimer();
            if (timer == null)
                return;

            // Vincula el texto del label al secondsLeft del timer, formateado mm:ss
            timerLabel.textProperty().unbind();
            timerLabel.textProperty().bind(
                    Bindings.createStringBinding(
                            () -> formatMMSS(timer.secondsLeftProperty().get()),
                            timer.secondsLeftProperty()));

            // Si venimos de otra pantalla y estaba pausado, reanudar (sin reiniciar)
            if (timer.secondsLeftProperty().get() > 0) {
                timer.start();
            }
        } catch (Exception e) {
            logger.error("Error al configurar Pomodoro HUD", e);
        }
    }

    private String formatMMSS(int total) {
        if (total < 0)
            total = 0;
        int mm = total / 60;
        int ss = total % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    // ===== RENDERIZAR =====
    private void renderLeccion() {
        if (root == null) {
            logger.warn("root es null al intentar renderizar");
            return;
        }
        if (leccionActual == null) {
            logger.warn("No hay lección actual para renderizar");
            return;
        }

        String titulo = leccionActual.titulo();
        String contenido = leccionActual.contenido();
        String tipoContenido = leccionActual.tipoContenido();
        String urlVideo = leccionActual.url_video();
        String contenidoHtml = leccionActual.contenidoHtml();
        String pdfUrl = leccionActual.pdfUrl();

        // 🔹 Actualizar título y descripción
        Label tituloLabel = (Label) root.lookup("#tituloLeccion");
        if (tituloLabel != null) {
            tituloLabel.setText(titulo != null ? titulo : "Lección");
        }

        Label descripLabel = (Label) root.lookup("#descripcionLeccion");
        if (descripLabel != null) {
            descripLabel.setText(contenido != null ? contenido : "");
        }

        // 🔹 Cargar contenido en WebView según el tipo
        WebView webView = (WebView) root.lookup("#webView");
        if (webView != null) {
            if (tipoContenido != null) {
                switch (tipoContenido.toUpperCase()) {
                    case "TEORIA" -> {
                        // Cargar HTML si está disponible
                        if (contenidoHtml != null && !contenidoHtml.isBlank()) {
                            webView.getEngine().loadContent(contenidoHtml);
                            logger.debug("Contenido HTML cargado para lección: {}", titulo);
                        } else {
                            // Si no hay HTML, mostrar contenido de texto básico
                            String basicHtml = """
                                    <html>
                                    <head>
                                        <style>
                                            body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
                                            h2 { color: #0066cc; }
                                        </style>
                                    </head>
                                    <body>
                                        <h2>%s</h2>
                                        <p>%s</p>
                                    </body>
                                    </html>
                                    """.formatted(titulo, contenido != null ? contenido : "");
                            webView.getEngine().loadContent(basicHtml);
                        }
                    }
                    case "VIDEO" -> {
                        // Cargar video/enlace
                        if (urlVideo != null && !urlVideo.isBlank()) {
                            String videoHtml = """
                                    <html>
                                    <head>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                    </head>
                                    <body style="margin:0; background-color:#080736; display:flex; justify-content:center; align-items:center; height:100vh; color:white; font-family:Arial;">
                                        <a href="%s" style="color:white; font-size:30px; text-decoration:none; padding:15px 25px; border:2px solid white; border-radius:12px;">
                                          ▶ Ver video en YouTube
                                        </a>
                                    </body>
                                    </html>
                                    """
                                    .formatted(urlVideo);
                            webView.getEngine().loadContent(videoHtml);
                            logger.debug("Enlace de video cargado: {}", urlVideo);
                        } else {
                            webView.getEngine().loadContent(
                                    "<html><body style='background:#080736; color:white; text-align:center; padding:50px;'>No hay video disponible</body></html>");
                        }
                    }
                    case "PRACTICA" -> {
                        // Cargar interfaz de práctica/consola
                        if (contenidoHtml != null && !contenidoHtml.isBlank()) {
                            // Si hay HTML personalizado, agregamos un editor de código al final
                            String htmlConEditor = contenidoHtml.replace("</body>",
                                    """
                                            <h3 style='color: #0066cc; margin-top: 30px;'>Editor de Código:</h3>
                                            <textarea id='codeEditor' style='width: 100%%; height: 300px; font-family: "Courier New", monospace;
                                                background: #263238; color: #aed581; padding: 15px; border-radius: 5px;
                                                border: 2px solid #4DA3FF; font-size: 14px;'
                                                placeholder='// Escribe tu código aquí...'></textarea>
                                            <button onclick='ejecutarCodigo()' style='margin-top: 15px; padding: 10px 20px; background: #4DA3FF;
                                                color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer;'>
                                                ▶ Ejecutar Código
                                            </button>
                                            <div id='output' style='margin-top: 20px; padding: 15px; background: #1e1e1e;
                                                color: #d4d4d4; border-radius: 5px; font-family: monospace; min-height: 100px;'></div>
                                            <script>
                                            function ejecutarCodigo() {
                                                let output = document.getElementById('output');
                                                output.innerHTML = '<p style="color: #4ec9b0;">✓ Código enviado para revisión</p>' +
                                                    '<p style="color: #dcdcaa;">Nota: La ejecución real de código Java requiere un compilador.</p>';
                                            }
                                            </script>
                                            </body>
                                            """);
                            webView.getEngine().loadContent(htmlConEditor);
                        } else {
                            String practicaHtml = """
                                    <html>
                                    <head>
                                        <style>
                                            body { font-family: 'Courier New', monospace; padding: 20px; background: #f5f5f5; }
                                            h2 { color: #4ec9b0; }
                                            .instructions { background: #fff9c4; padding: 15px; border-left: 4px solid #fbc02d; margin: 20px 0; }
                                            .editor { width: 100%%; height: 300px; font-family: 'Courier New', monospace;
                                                background: #263238; color: #aed581; padding: 15px; border-radius: 5px;
                                                border: 2px solid #4DA3FF; font-size: 14px; }
                                            button { margin-top: 15px; padding: 10px 20px; background: #4DA3FF;
                                                color: white; border: none; border-radius: 5px; font-size: 16px; cursor: pointer; }
                                            button:hover { background: #0080cc; }
                                            .output { margin-top: 20px; padding: 15px; background: #1e1e1e;
                                                color: #d4d4d4; border-radius: 5px; font-family: monospace; min-height: 100px; }
                                        </style>
                                    </head>
                                    <body>
                                        <h2>Ejercicio Práctico</h2>
                                        <div class="instructions">
                                            <strong>Instrucciones:</strong><br>
                                            %s
                                        </div>
                                        <h3 style='color: #0066cc;'>Editor de Código:</h3>
                                        <textarea id='codeEditor' class='editor' placeholder='// Escribe tu código aquí...'></textarea>
                                        <br>
                                        <button onclick='ejecutarCodigo()'>▶ Ejecutar Código</button>
                                        <div id='output' class='output'>
                                            <p style='color: #888;'>La salida aparecerá aquí...</p>
                                        </div>
                                        <script>
                                        function ejecutarCodigo() {
                                            let output = document.getElementById('output');
                                            output.innerHTML = '<p style="color: #4ec9b0;">✓ Código enviado para revisión</p>' +
                                                '<p style="color: #dcdcaa;">Nota: La ejecución real de código Java requiere un compilador.</p>';
                                        }
                                        </script>
                                    </body>
                                    </html>
                                    """
                                    .formatted(contenido != null ? contenido : "Completa el ejercicio");
                            webView.getEngine().loadContent(practicaHtml);
                        }
                    }
                    case "PDF" -> {
                        // Mostrar enlace al PDF
                        if (pdfUrl != null && !pdfUrl.isBlank()) {
                            String pdfHtml = """
                                    <html>
                                    <body style="margin:0; background-color:#080736; display:flex; justify-content:center; align-items:center; height:100vh; color:white; font-family:Arial;">
                                        <a href="%s" style="color:white; font-size:30px; text-decoration:none; padding:15px 25px; border:2px solid white; border-radius:12px;">
                                          📄 Abrir PDF
                                        </a>
                                    </body>
                                    </html>
                                    """
                                    .formatted(pdfUrl);
                            webView.getEngine().loadContent(pdfHtml);
                        } else {
                            webView.getEngine().loadContent(
                                    "<html><body style='background:#080736; color:white; text-align:center; padding:50px;'>No hay PDF disponible</body></html>");
                        }
                    }
                    default ->
                        webView.getEngine().loadContent("<html><body style='background:#f5f5f5; padding:20px;'><p>"
                                + (contenido != null ? contenido : "Contenido no disponible") + "</p></body></html>");
                }
            } else {
                webView.getEngine().loadContent(
                        "<html><body style='background:#f5f5f5; padding:20px;'><p>Tipo de contenido no especificado</p></body></html>");
            }
        }

        logger.debug("Lección renderizada: {} (Tipo: {})", titulo, tipoContenido);
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
        if (numeroActual == 4) {
            irAlQuiz((Node) e.getSource());
            return;
        }
        int siguiente = numeroActual + 1;
        irA(siguiente, (Node) e.getSource());
    }

    private void irAlQuiz(Node source) {
        try {
            navigator.goToWithInit(
                    "/views/QuizPlantilla.fxml",
                    "Quiz Final",
                    controllerFactory,
                    (source != null ? source : root),
                    (QuizController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.cargarQuiz(seccionOrden);
                    });
        } catch (Exception e) {
            logger.error("Error al abrir quiz", e);
            uiHelper.showError("Error al abrir quiz", e.getMessage());
        }
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

            String ruta = "/views/LeccionPlantilla.fxml";
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
                    });
        } catch (Exception ex) {
            logger.error("Error al cambiar de lección", ex);
            uiHelper.showError("Error al cambiar de lección", ex.getMessage());
        }
    }

    @FXML
    private void volverACurso() {
        try {
            if (cursoId <= 0) {
                // Si no hay cursoId, intentamos ir al home o mostrar error
                uiHelper.showInfo("Navegación", "No hay curso asociado para volver.");
                goHome();
                return;
            }

            navigator.goToWithInit(
                    "/views/PlantillaCurso.fxml",
                    "Curso",
                    controllerFactory,
                    root,
                    (CursoController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.setCursoActual(cursoId, "Java Básico");
                    });
        } catch (Exception e) {
            logger.error("Error al volver al curso", e);
            uiHelper.showError("Error de navegación", e.getMessage());
        }
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
    }

    @FXML
    private void goForum() {
        navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, null);
    }

    @FXML
    private void goProfile() {
        navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, null);
    }

    @FXML
    private void goTienda() {
        navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
    }

    @FXML
    private void goIglu() {
        navigator.goTo("/views/Iglu.fxml", "STELLA - Iglu", controllerFactory, null);
    }

    @FXML
    private void goChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Chatbot.fxml"));
            if (controllerFactory != null)
                loader.setControllerFactory(controllerFactory::apply);

            Parent popupRoot = loader.load();
            Scene popupScene = new Scene(popupRoot, 1100, 750);
            popupScene.setFill(Color.TRANSPARENT);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo chatbot", e);
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            AppServices.cerrarSesion();
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error cerrando sesión", e);
        }
    }
}