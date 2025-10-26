package Infrastructure.controllers;

import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Infrastructure.ui.NavigationManager;
import Infrastructure.ui.UIFeedbackHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.function.Function;

/**
 * Controlador de la vista Pomodoro.
 * No maneja lógica de negocio: delega todo a servicios inyectados
 * por ControllerControladores y a helpers de interfaz (UIFeedbackHelper, NavigationManager).
 */
public class PomodoroController {

    // ====== Dependencias (inyectadas por ControllerControladores) ======
    private final SesionPomodoroService sesionPomodoroService; // <-- back: iniciar/guardar sesión
    private final PomodoroTimer pomodoroTimer;                 // <-- back: lógica del temporizador
    private Function<Class<?>, Object> controllerFactory;      // <-- inyectada desde ControllerControladores

    // ====== Helpers de UI (front) ======
    private final NavigationManager navigator = new NavigationManager();
    private final UIFeedbackHelper uiHelper = new UIFeedbackHelper();

    // ====== Nodos FXML ======
    @FXML private Label timerLabel;
    @FXML private Button startButton, pauseButton, confirmButton;
    @FXML private Button btn3min, btn5min, btn8min, btn10min;
    @FXML private Button btn25min, btn30min, btn45min, btn60min;
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    @FXML private ToggleGroup quizGroup;
    @FXML private Label quizResult;

    // ====== Estado mínimo ======
    private int minutosSeleccionados;

    // ====== Constructores ======
    public PomodoroController(SesionPomodoroService sesionPomodoroService,
                              PomodoroTimer pomodoroTimer) {
        this.sesionPomodoroService = sesionPomodoroService;
        this.pomodoroTimer = pomodoroTimer;
    }

    // Ctor vacío (para FXMLLoader si aún no usa la factory)
    public PomodoroController() {
        this.sesionPomodoroService = null;
        this.pomodoroTimer = PomodoroTimer.getInstance(); // fallback
    }

    // ====== Setter requerido por ControllerControladores ======
    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ====== Ciclo de vida ======
    @FXML
    public void initialize() {
        setupButtons();
        setupKeyboardShortcuts();

        // Vincula la etiqueta del temporizador al PomodoroTimer
        if (timerLabel != null && pomodoroTimer != null) {
            uiHelper.bindTimerLabel(timerLabel, pomodoroTimer);
        }
    }

    private void setupKeyboardShortcuts() {
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

    private void setupButtons() {
        if (startButton != null) startButton.setOnAction(e -> startPomodoro());
        if (pauseButton != null) pauseButton.setOnAction(e -> pausePomodoro());
        if (confirmButton != null) confirmButton.setOnAction(e -> confirmarTiempo());

        setupTimeButton(btn3min, 3);
        setupTimeButton(btn5min, 5);
        setupTimeButton(btn8min, 8);
        setupTimeButton(btn10min, 10);
        setupTimeButton(btn25min, 25);
        setupTimeButton(btn30min, 30);
        setupTimeButton(btn45min, 45);
        setupTimeButton(btn60min, 60);
    }

    private void setupTimeButton(Button btn, int minutes) {
        if (btn != null) {
            btn.setOnAction(e -> {
                minutosSeleccionados = minutes;
                uiHelper.highlightSelectedButton(btn);
            });
        }
    }

    // ====== Eventos ======
    private void startPomodoro() {
        if (sesionPomodoroService != null)
            sesionPomodoroService.iniciarSesion(minutosSeleccionados); // <-- lógica de back
        pomodoroTimer.start(); // solo temporizador
    }

    private void pausePomodoro() {
        pomodoroTimer.pause();
    }

    private void confirmarTiempo() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }

        if (sesionPomodoroService != null)
            sesionPomodoroService.confirmarSesion(minutosSeleccionados); // <-- back: guardar sesión

        navigator.goTo("/views/PomodoroDescanso.fxml", "Descanso",
                controllerFactory, confirmButton);
    }

    @FXML
    private void onQuizValidate() {
        // <-- asumo que el back valida las respuestas
        if (sesionPomodoroService != null && quizGroup != null) {
            String resultado = sesionPomodoroService.validarQuiz(quizGroup);
            uiHelper.updateQuizFeedback(quizResult, resultado);
        }
    }

    // ====== Navegación inferior ======
    @FXML private void goHome()        { navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn); }
    @FXML private void goForum()       { uiHelper.showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements(){ uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()     { uiHelper.showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }
}
//holap
