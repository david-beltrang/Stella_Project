package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.Listado_Cursos.CursoResponse;
import Application.dtos.Listado_Cursos.CursosResponse;
import Application.dtos.Listado_Cursos.InscripcionRequest;
import Application.services.ListarCursosService;
import Application.services.SeccionesService;
import Application.services.PomodoroTimer;
import Application.services.CursoUIService;
import Application.services.UsuarioStatsService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrincipalController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(PrincipalController.class);

    private Function<Class<?>, Object> controllerFactory;

    @FXML
    private TextField searchField;
    @FXML
    private ScrollPane misCursosScroll;
    @FXML
    private ScrollPane cursosDisponiblesScroll;
    @FXML
    private HBox misCursosContainer;
    @FXML
    private HBox cursosDisponiblesContainer;
    @FXML
    private Label noCoursesLabel;
    @FXML
    private Button leftArrow, rightArrow;
    @FXML
    private Button leftArrowMisCursos, rightArrowMisCursos;
    @FXML
    private Button homeBtn, forumBtn, achievementsBtn, profileBtn;
    @FXML
    private Button homeBtn2, forumBtn2, achievementsBtn2;
    @FXML
    private Button logoutBtn;
    @FXML
    private AnchorPane root;

    @FXML
    private Label timerLabel;

    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;

    // ====== DEPENDENCIAS ======
    private final ListarCursosService listarCursosService;
    private final SeccionesService seccionesService;
    private final CursoUIService cursoUIService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navegacion navigator = new Navegacion();
    private final UsuarioStatsService usuarioStatsService;

    // ====== VARIABLES DE ESTADO ======
    private Integer usuarioActualId;
    private CursosResponse cursosActuales;
    private final PomodoroTimer pomodoroTimer;

    // ====== CONSTRUCTOR ======

    public PrincipalController(ListarCursosService listarCursosService, SeccionesService seccionesService,
            UsuarioStatsService usuarioStatsService) {
        this.listarCursosService = listarCursosService;
        this.seccionesService = seccionesService;
        this.usuarioStatsService = usuarioStatsService;
        this.cursoUIService = new CursoUIService();
        this.pomodoroTimer = AppServices.getPomodoroTimer();
    }

    public PrincipalController() {
        // Requerido por FXMLLoader
        this.listarCursosService = AppServices.getListarCursosService();
        this.seccionesService = AppServices.getSeccionesService();
        this.usuarioStatsService = null;
        this.cursoUIService = new CursoUIService();
        this.pomodoroTimer = AppServices.getPomodoroTimer();
    }

    // ==============================
    // MÉTODOS PRINCIPALES
    // ==============================

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarUsuario();
        configurarFlechas();
        configurarBusqueda();

        if (logoutBtn != null) {
            logoutBtn.setOnAction(e -> cerrarSesion());
        }

        // Inicializa el HUD Pomodoro dinámico
        setupPomodoroHud();
        configurarAtajoF12(); // Atajo F12 para saltar al descanso

        if (usuarioActualId != null) {
            cargarCursosDesdeBD();
        }
    }

    // ==============================
    // MÉTODOS AUXILIARES
    // ==============================

    private void setupPomodoroHud() {
        if (timerLabel == null)
            return;

        PomodoroTimer t = AppServices.getPomodoroTimer();
        if (t == null)
            return;

        // Vincular el texto del label al temporizador global
        timerLabel.textProperty().unbind();
        timerLabel.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatMMSS(t.secondsLeftProperty().get()),
                        t.secondsLeftProperty()));
    }

    private String formatMMSS(int total) {
        if (total < 0)
            total = 0;
        int mm = total / 60, ss = total % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    // Configurar atajo F12 para saltar al descanso
    private void configurarAtajoF12() {
        if (root == null)
            return;

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.getCode().toString().equals("F12")) {
                        if (pomodoroTimer != null) {
                            // Cambiar al tiempo de descanso
                            int breakTime = pomodoroTimer.getBreakTimeSeconds();
                            pomodoroTimer.setSecondsLeft(breakTime);
                            pomodoroTimer.start();
                            uiHelper.showInfo("Pomodoro Finalizado",
                                    "Iniciando tiempo de descanso (" + (breakTime / 60) + " min)...");
                        }
                        event.consume();
                    }
                });
            }
        });
    }

    // ==============================
    // CARGA DE USUARIO Y CURSOS
    // ==============================
    public void inicializarUsuario() {
        var usuario = AppServices.getUsuarioActual();
        if (usuario != null) {
            usuarioActualId = usuario.id();
            logger.info("Usuario activo: {}", usuario.nombre());
            cargarCursosDesdeBD();
            try {
                if (usuarioStatsService != null) {
                    int pescaditos = usuarioStatsService.obtenerPescaditos();
                    if (pescaditosLabel != null) {
                        pescaditosLabel.setText(String.valueOf(pescaditos));
                        logger.debug("Pescaditos cargados en Principal: {}", pescaditos);
                    }
                }
            } catch (Exception e) {
                logger.error("Error cargando pescaditos", e);
                if (pescaditosLabel != null) {
                    pescaditosLabel.setText("0");
                }
            }
            cargarRacha();
        } else {
            usuarioActualId = null;
            logger.warn("No hay usuario activo. Saltando carga de cursos.");
        }
    }

    private void cargarRacha() {
        if (rachaLabel == null) {
            return;
        }
        try {
            if (usuarioStatsService != null) {
                int racha = usuarioStatsService.obtenerRachaDias();
                rachaLabel.setText(String.valueOf(racha));
                logger.debug("Racha cargada en Principal: {}", racha);
            }
        } catch (Exception e) {
            logger.error("Error cargando racha", e);
            rachaLabel.setText("0");
        }
    }

    private void cargarCursosDesdeBD() {
        try {
            cursosActuales = listarCursosService.obtenerCursosCompletos(usuarioActualId);
            cargarMisCursos();
            cargarCursosDisponibles();
        } catch (Exception e) {
            uiHelper.showError("Error cargando cursos", e.getMessage());
        }
    }

    private void cargarMisCursos() {
        misCursosContainer.getChildren().clear();
        if (cursosActuales == null || cursosActuales.cursosUsuario().isEmpty()) {
            noCoursesLabel.setVisible(true);
            return;
        }
        noCoursesLabel.setVisible(false);
        for (CursoResponse curso : cursosActuales.cursosUsuario()) {
            // Delegar creación de UI al servicio
            VBox card = cursoUIService.crearTarjetaCursoUsuario(curso, this::abrirCurso);
            misCursosContainer.getChildren().add(card);
        }
    }

    private void abrirCurso(CursoResponse curso) {
        if (controllerFactory == null) {
            uiHelper.showError("Error", "No hay factory de controladores configurada.");
            return;
        }
        try {
            // Delegar validación al servicio
            var secciones = seccionesService.ListarSeccionesConLecciones(curso.id());
            if (secciones == null || secciones.isEmpty()) {
                uiHelper.showInfo("Curso: " + curso.titulo(),
                        "El contenido de este curso estará disponible próximamente.");
                return;
            }
            // Navegación delegada
            CursoController cursoCtrl = (CursoController) controllerFactory.apply(CursoController.class);
            cursoCtrl.setCursoActual(curso.id(), curso.titulo());
            navigator.goTo("/views/PlantillaCurso.fxml", "STELLA - " + curso.titulo(), controllerFactory, root);
        } catch (Exception ex) {
            logger.error("Error cargando contenido del curso: {}", curso.titulo(), ex);
            uiHelper.showError("Error cargando contenido", ex.getMessage());
        }
    }

    private void cargarCursosDisponibles() {
        cursosDisponiblesContainer.getChildren().clear();
        if (cursosActuales == null)
            return;
        for (CursoResponse curso : cursosActuales.cursosDisponibles()) {
            // Delegar creación de UI al servicio
            VBox card = cursoUIService.crearTarjetaCursoDisponible(curso, c -> {
                if (usuarioActualId == null) {
                    uiHelper.showError("Error", "No hay un usuario activo para inscribir cursos.");
                    return;
                }
                inscribirCurso(c.id());
            });
            cursosDisponiblesContainer.getChildren().add(card);
        }
    }

    private void inscribirCurso(int cursoId) {
        try {
            InscripcionRequest request = new InscripcionRequest(usuarioActualId, cursoId);
            cursosActuales = listarCursosService.inscribirCurso(request);
            cargarMisCursos();
            cargarCursosDisponibles();
            uiHelper.showInfo("Inscripción exitosa", "El curso fue agregado correctamente.");
        } catch (Exception e) {
            uiHelper.showError("Error al inscribir curso", e.getMessage());
        }
    }

    // ==============================
    // BÚSQUEDA
    // ==============================
    private void configurarBusqueda() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> buscarCurso());
        }
    }

    @FXML
    private void buscarCurso() {
        String texto = searchField.getText().toLowerCase();
        if (texto.isBlank()) {
            cargarCursosDisponibles();
            return;
        }
        var filtrados = cursosActuales.cursosDisponibles().stream()
                .filter(c -> c.titulo().toLowerCase().contains(texto))
                .collect(Collectors.toList());

        cursosDisponiblesContainer.getChildren().clear();
        if (filtrados.isEmpty()) {
            uiHelper.showInfo("Sin resultados", "No se encontraron cursos con ese nombre.");
        } else {
            for (CursoResponse curso : filtrados) {
                // Delegar creación de UI al servicio
                VBox card = cursoUIService.crearTarjetaCursoDisponible(curso, c -> {
                    if (usuarioActualId == null) {
                        uiHelper.showError("Error", "No hay un usuario activo para inscribir cursos.");
                        return;
                    }
                    inscribirCurso(c.id());
                });
                cursosDisponiblesContainer.getChildren().add(card);
            }
        }
    }

    // ==============================
    // SCROLL Y FLECHAS
    // ==============================
    private void configurarFlechas() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setOnAction(e -> scrollLeft());
            rightArrow.setOnAction(e -> scrollRight());
        }
        if (leftArrowMisCursos != null && rightArrowMisCursos != null) {
            leftArrowMisCursos.setOnAction(e -> scrollLeftMisCursos());
            rightArrowMisCursos.setOnAction(e -> scrollRightMisCursos());
        }
    }

    @FXML
    private void scrollLeft() {
        scrollHorizontally(cursosDisponiblesScroll, -0.3);
    }

    @FXML
    private void scrollRight() {
        scrollHorizontally(cursosDisponiblesScroll, 0.3);
    }

    @FXML
    private void scrollLeftMisCursos() {
        scrollHorizontally(misCursosScroll, -0.3);
    }

    @FXML
    private void scrollRightMisCursos() {
        scrollHorizontally(misCursosScroll, 0.3);
    }

    private void scrollHorizontally(ScrollPane scrollPane, double delta) {
        double newValue = scrollPane.getHvalue() + delta;
        newValue = Math.max(0, Math.min(1, newValue));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.hvalueProperty(), newValue)));
        timeline.play();
    }

    // ==============================
    // NAVEGACIÓN Y UI
    // ==============================
    @FXML
    private void goPomodoro() {
        try {
            navigator.goTo("/views/Pomodoro.fxml", "STELLA - Pomodoro", controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir Pomodoro", e.getMessage());
        }
    }

    @FXML
    private void goGamificacion() {
        try {
            navigator.goTo("/views/Gamificacion.fxml", "STELLA - Gamificación", controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir Gamificación", e.getMessage());
        }
    }

    @FXML
    private void goHome() {
        uiHelper.showInfo("Inicio", "Ya estás en la pantalla principal.");
    }

    @FXML
    private void goForum() {
        try {
            navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, root);
        } catch (Exception e) {
            logger.error("Error al navegar al foro", e);
            uiHelper.showError("Error al navegar al foro", e.getMessage());
        }
    }

    @FXML
    private void goAchievements() {
        uiHelper.showInfo("Logros", "Pantalla de logros aún no implementada.");
    }

    @FXML
    private void goProfile() {
        try {
            navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, root);
        } catch (Exception e) {
            logger.error("Error al navegar al perfil", e);
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir la Tienda", e.getMessage());
        }
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

    // ==============================
    // CERRAR SESIÓN
    // ==============================
    @FXML
    private void cerrarSesion() {
        try {
            AppServices.cerrarSesion();
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, root);
        } catch (Exception e) {
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
        }
    }
}
