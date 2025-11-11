package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.Listado_Cursos.CursoResponse;
import Application.dtos.Listado_Cursos.CursosResponse;
import Application.dtos.Listado_Cursos.InscripcionRequest;
import Application.services.ListarCursosService;
import Application.services.SeccionesService;
import Application.services.PomodoroTimer;       // ⏱
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;            // ⏱
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

public class PrincipalController implements Initializable {

    // ===== Factory global =====
    private Function<Class<?>, Object> controllerFactory;

    // ====== ELEMENTOS FXML ======
    @FXML private TextField searchField;
    @FXML private ScrollPane misCursosScroll;
    @FXML private ScrollPane cursosDisponiblesScroll;
    @FXML private HBox misCursosContainer;
    @FXML private HBox cursosDisponiblesContainer;
    @FXML private Label noCoursesLabel;
    @FXML private Button leftArrow, rightArrow;
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;
    @FXML private Button homeBtn2, forumBtn2, achievementsBtn2;
    @FXML private Button logoutBtn;
    @FXML private AnchorPane root;

    // ⏱ NUEVO: label del reloj en principal
    @FXML private Label lblTiempoPomodoro;

    // ====== DEPENDENCIAS ======
    private final ListarCursosService listarCursosService;
    private final SeccionesService seccionesService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navigacion navigator = new Navigacion();

    // ====== VARIABLES DE ESTADO ======
    private Integer usuarioActualId;
    private CursosResponse cursosActuales;

    // ====== CONSTRUCTOR ======
    public PrincipalController(ListarCursosService listarCursosService, SeccionesService seccionesService) {
        this.listarCursosService = listarCursosService;
        this.seccionesService = seccionesService;
    }

    // ==============================
    //       MÉTODOS PRINCIPALES
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

        // ⏱ Enlazar HUD Pomodoro (solo UI)
        setupPomodoroHud();

        if (usuarioActualId != null) {
            cargarCursosDesdeBD();
        }
    }

    // ⏱ Enlaza el label al PomodoroTimer global y lo deja en pausa en Principal
    private void setupPomodoroHud() {
        if (lblTiempoPomodoro == null) return;
        PomodoroTimer t = AppServices.getPomodoroTimer();
        if (t == null) return;

        lblTiempoPomodoro.textProperty().unbind();
        lblTiempoPomodoro.textProperty().bind(
                Bindings.createStringBinding(
                        () -> formatMMSS(t.secondsLeftProperty().get()),
                        t.secondsLeftProperty()
                )
        );

        // En principal solo se muestra (pausado)
        try { t.pause(); } catch (Exception ignore) {}
    }

    private String formatMMSS(int total) {
        if (total < 0) total = 0;
        int mm = total / 60, ss = total % 60;
        return String.format("%02d:%02d", mm, ss);
    }

    public void inicializarUsuario() {
        var usuario = AppServices.getUsuarioActual();
        if (usuario != null) {
            usuarioActualId = usuario.id();
            System.out.println("Usuario activo: " + usuario.nombre());
            cargarCursosDesdeBD();
        } else {
            usuarioActualId = null;
            System.err.println("[WARN] No hay usuario activo. Saltando carga de cursos.");
        }
    }

    // ====== CARGA DE DATOS ======
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
            VBox card = crearTarjetaCurso(curso);
            misCursosContainer.getChildren().add(card);
        }
    }

    private VBox crearTarjetaCurso(CursoResponse curso) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.6); -fx-background-radius: 15; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        Label nivel = new Label("Nivel: " + curso.nivel());
        nivel.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 14;");

        Button btn = new Button("Continuar");
        btn.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold;");

        btn.setOnAction(e -> {
            if (controllerFactory == null) {
                uiHelper.showError("Error", "No hay factory de controladores configurada.");
                return;
            }
            try {
                var secciones = seccionesService.ListarSeccionesConLecciones(curso.id());
                if (secciones == null || secciones.isEmpty()) {
                    uiHelper.showInfo("Curso: " + curso.titulo(), "El contenido de este curso estará disponible próximamente.");
                    return;
                }
                CursoController cursoCtrl = (CursoController) controllerFactory.apply(CursoController.class);
                cursoCtrl.setCursoActual(curso.id(), curso.titulo());
                navigator.goTo("/views/PlantillaCurso.fxml", "STELLA - " + curso.titulo(), controllerFactory, btn);
            } catch (Exception ex) {
                uiHelper.showError("Error cargando contenido", ex.getMessage());
                ex.printStackTrace();
            }
        });

        card.getChildren().addAll(title, nivel, btn);
        return card;
    }

    private void cargarCursosDisponibles() {
        cursosDisponiblesContainer.getChildren().clear();
        if (cursosActuales == null) return;
        for (CursoResponse curso : cursosActuales.cursosDisponibles()) {
            VBox card = crearTarjetaCursoDisponible(curso);
            cursosDisponiblesContainer.getChildren().add(card);
        }
    }

    private VBox crearTarjetaCursoDisponible(CursoResponse curso) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.65); -fx-background-radius: 20; -fx-padding: 15;");
        card.setAlignment(Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        Button agregar = new Button("Agregar");
        agregar.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white;");
        agregar.setOnAction(e -> {
            if (usuarioActualId == null) {
                uiHelper.showError("Error", "No hay un usuario activo para inscribir cursos.");
                return;
            }
            inscribirCurso(curso.id());
        });

        card.getChildren().addAll(title, agregar);
        return card;
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
                VBox card = crearTarjetaCursoDisponible(curso);
                cursosDisponiblesContainer.getChildren().add(card);
            }
        }
    }

    private void configurarFlechas() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setOnAction(e -> scrollLeft());
            rightArrow.setOnAction(e -> scrollRight());
        }
    }

    @FXML private void scrollLeft()  { scrollHorizontally(cursosDisponiblesScroll, -0.3); }
    @FXML private void scrollRight() { scrollHorizontally(cursosDisponiblesScroll,  0.3); }

    private void scrollHorizontally(ScrollPane scrollPane, double delta) {
        double newValue = scrollPane.getHvalue() + delta;
        newValue = Math.max(0, Math.min(1, newValue));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.hvalueProperty(), newValue))
        );
        timeline.play();
    }

    @FXML
    private void goPomodoro() {
        try {
            navigator.goTo("/views/Pomodoro.fxml", "STELLA - Pomodoro", controllerFactory, null);
        } catch (Exception e) {
            uiHelper.showError("Error al abrir Pomodoro", e.getMessage());
        }
        // En la pantalla de Pomodoro el controller ya pausa/gestiona el timer
    }

    // ====== NAVEGACIÓN INFERIOR ======
    @FXML private void goHome()        { uiHelper.showInfo("Inicio", "Ya estás en la pantalla principal."); }
    @FXML private void goForum()       { uiHelper.showInfo("Foro", "Pantalla de foro aún no implementada."); }
    @FXML private void goAchievements(){ uiHelper.showInfo("Logros", "Pantalla de logros aún no implementada."); }
    @FXML private void goProfile()     { uiHelper.showInfo("Perfil", "Pantalla de perfil aún no implementada."); }
    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
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
            System.err.println("[CHATBOT] Error abriendo chatbot: " + e.getMessage());
            e.printStackTrace();
        }
    }



    // ====== CERRAR SESIÓN ======
    private void cerrarSesion() {
        AppServices.cerrarSesion();
        if (controllerFactory != null) {
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, logoutBtn);
        } else {
            navigator.cambiarPantalla("/views/Login.fxml", logoutBtn);
        }
    }
}
