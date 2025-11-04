package Infrastructure.controllers;

import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Controlador unificado para las vistas:
 *  - Pomodoro.fxml
 *  - PomodoroDescanso.fxml
 *  - PomodoroTiempoFinalizado.fxml
 *
 * Se encarga de manejar:
 *  1. Selección de tiempo de estudio.
 *  2. Selección de tiempo de descanso.
 *  3. Pantalla final de sesión.
 */
public class PomodoroController {

    // ====== Dependencias inyectadas ======
    private final SesionPomodoroService sesionPomodoroService;
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ====== Dependencias de interfaz ======
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ====== FXML ======
    @FXML private AnchorPane root;
    @FXML private Label timerLabel;

    // Botones genéricos (navegación inferior)
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    // Botones de tiempo Pomodoro
    @FXML private Button btn3min, btn5min, btn8min, btn10min;
    @FXML private Button btn25min, btn30min, btn45min, btn60min;

    // Botones de control
    @FXML private Button startButton, pauseButton, confirmarButton;

    // ====== Estado interno ======
    private int minutosSeleccionados;
    private final List<Button> botonesTiempo = new ArrayList<>();

    // ====== Constructores ======
    public PomodoroController(SesionPomodoroService sesionPomodoroService,
                              PomodoroTimer pomodoroTimer) {
        this.sesionPomodoroService = sesionPomodoroService;
        this.pomodoroTimer = pomodoroTimer;
    }

    public PomodoroController() {
        this.sesionPomodoroService = null;
        this.pomodoroTimer = PomodoroTimer.getInstance();
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ====== Ciclo de vida ======
    @FXML
    public void initialize() {
        if (root == null) return;

        if (confirmarButton != null && (btn25min != null || btn30min != null)) {
            configurarVistaPomodoro();
        } else if (confirmarButton != null && (btn3min != null || btn5min != null)) {
            configurarVistaDescanso();
        } else if (root.lookup("#homeBtn") != null && root.lookup("#achievementsBtn") != null) {
            configurarVistaFinal();
        }
    }

    // ====== VISTA POMODORO (estudio) ======
    private void configurarVistaPomodoro() {
        setupTimeButtonsPomodoro();
        if (confirmarButton != null) confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
        configurarAtajoTeclado();
    }

    // ====== VISTA DESCANSO ======
    private void configurarVistaDescanso() {
        setupTimeButtonsDescanso();
        if (confirmarButton != null) confirmarButton.setOnAction(e -> confirmarTiempoDeDescanso());
    }

    // ====== VISTA FINAL ======
    private void configurarVistaFinal() {
        Button newSessionBtn = (Button) root.lookup("#newStudySession");
        Button exitBtn = (Button) root.lookup("#exitToHome");

        if (newSessionBtn != null)
            newSessionBtn.setOnAction(e -> newStudySession());

        if (exitBtn != null)
            exitBtn.setOnAction(e -> exitToHome());
    }

    // ====== Configuración de botones ======
    private void setupTimeButtonsPomodoro() {
        setupTimeButton(btn25min, 25);
        setupTimeButton(btn30min, 30);
        setupTimeButton(btn45min, 45);
        setupTimeButton(btn60min, 60);
    }

    private void setupTimeButtonsDescanso() {
        setupTimeButton(btn3min, 3);
        setupTimeButton(btn5min, 5);
        setupTimeButton(btn8min, 8);
        setupTimeButton(btn10min, 10);
    }

    // === Método genérico que asegura selección única ===
    private void setupTimeButton(Button btn, int minutes) {
        if (btn != null) {
            botonesTiempo.add(btn);

            btn.setOnAction(e -> {
                minutosSeleccionados = minutes;

                // Limpia estilos de todos los botones
                for (Button b : botonesTiempo) {
                    b.setStyle("-fx-background-color: #00BFA5; -fx-text-fill: white; -fx-font-weight: bold;");
                }

                // Aplica estilo al seleccionado
                btn.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 3;");

                // (Opcional, mantiene compatibilidad)
                uiHelper.highlightSelectedButton(btn);
            });
        }
    }

    // ====== Confirmaciones ======
    @FXML
    private void confirmarTiempoDeFoco() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }

        try {
            if (sesionPomodoroService != null) {
                sesionPomodoroService.iniciarSesion(
                        new Application.dtos.sesionEstudio.Pomodoro.IniciarSesionEstudioRequest(
                                1, minutosSeleccionados, 5)
                );
            }
            navigator.goTo("/views/PomodoroDescanso.fxml", "Descanso", controllerFactory, confirmarButton);
        } catch (Exception ex) {
            uiHelper.showError("Error al confirmar tiempo", ex.getMessage());
        }
    }

    @FXML
    private void confirmarTiempoDeDescanso() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo de descanso.");
            return;
        }

        try {
            pomodoroTimer.start();
            navigator.goTo("/views/PomodoroTiempoFinalizado.fxml", "Tiempo finalizado", controllerFactory, confirmarButton);
        } catch (Exception ex) {
            uiHelper.showError("Error al confirmar descanso", ex.getMessage());
        }
    }

    @FXML
    private void newStudySession() {
        navigator.goTo("/views/Pomodoro.fxml", "Nueva sesión Pomodoro", controllerFactory, root);
    }

    @FXML
    private void exitToHome() {
        navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, root);
    }

    // ====== Atajo de teclado ======
    private void configurarAtajoTeclado() {
        if (timerLabel == null) return;
        timerLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                    if (ev.getCode().toString().equals("F12")) {
                        navigator.goTo("/views/PomodoroTiempoFinalizado.fxml",
                                "¡Tiempo terminado!", controllerFactory, homeBtn);
                        ev.consume();
                    }
                });
            }
        });
    }

    // ====== Navegación inferior ======
    @FXML private void goHome()        { navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn); }
    @FXML private void goForum()       { uiHelper.showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements(){ uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()     { uiHelper.showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }
}
