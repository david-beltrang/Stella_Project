package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService; // inyectado, pero NO se usa (sin lógica de negocio)
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

public class PomodoroController {

    // ====== Dependencias inyectadas (UI-only: NO negocio) ======
    private final SesionPomodoroService sesionPomodoroService; // disponible pero no usado aquí
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ====== UI ======
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ====== FXML ======
    @FXML private AnchorPane root;
    @FXML private Label timerLabel;

    // Botones inferiores
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    // Botones de tiempo Pomodoro (foco)
    @FXML private Button btn25min, btn30min, btn45min, btn60min;

    // Botones de tiempo Descanso
    @FXML private Button btn3min, btn5min, btn8min, btn10min;

    // Botones de control
    @FXML private Button startButton, pauseButton, confirmarButton;

    // ====== Estado UI ======
    private int minutosSeleccionados = 0;
    private final List<Button> botonesTiempo = new ArrayList<>();

    // ====== Constructores ======
    public PomodoroController(SesionPomodoroService sesionPomodoroService,
                              PomodoroTimer pomodoroTimer) {
        this.sesionPomodoroService = sesionPomodoroService; // NO se usa aquí
        this.pomodoroTimer = pomodoroTimer;
    }

    public PomodoroController() {
        this.sesionPomodoroService = AppServices.getSesionPomodoroService(); // NO se usa aquí
        this.pomodoroTimer = AppServices.getPomodoroTimer();
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ====== Ciclo de vida ======
    @FXML
    public void initialize() {
        if (root == null) return;

        // Listener UI-only: si el tiempo llega a 0, mostrar pantalla final. (Sin persistencia aquí)
        if (!AppServices.isPomodoroFinishListenerRegistrado() && pomodoroTimer != null) {
            pomodoroTimer.secondsLeftProperty().addListener((obs, ov, nv) -> {
                if (nv != null && nv.intValue() <= 0) {
                    navigator.goTo("/views/PomodoroTiempoFinalizado.fxml",
                            "Tiempo finalizado", controllerFactory, root);
                }
            });
            AppServices.setPomodoroFinishListenerRegistrado(true);
        }

        // En pantallas NO de estudio, mantener el timer en pausa
        try { if (pomodoroTimer != null) pomodoroTimer.pause(); } catch (Exception ignore) {}

        // Detectar qué vista es (config de foco, config de descanso o final)
        if (confirmarButton != null && (btn25min != null || btn30min != null)) {
            configurarVistaPomodoro();
        } else if (confirmarButton != null && (btn3min != null || btn5min != null)) {
            configurarVistaDescanso();
        } else if (root.lookup("#homeBtn") != null && root.lookup("#achievementsBtn") != null) {
            configurarVistaFinal();
        }

        configurarAtajoTeclado();
    }

    // ====== VISTA POMODORO (foco) ======
    private void configurarVistaPomodoro() {
        setupTimeButtonsPomodoro();
        if (confirmarButton != null) confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
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
        if (newSessionBtn != null) newSessionBtn.setOnAction(e -> newStudySession());
        if (exitBtn != null)      exitBtn.setOnAction(e -> exitToHome());
    }

    // ====== Configuración de botones de tiempo ======
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

    private void setupTimeButton(Button btn, int minutes) {
        if (btn == null) return;
        botonesTiempo.add(btn);
        btn.setOnAction(e -> {
            minutosSeleccionados = minutes;
            for (Button b : botonesTiempo) {
                b.setStyle("-fx-background-color: #00BFA5; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            btn.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 3;");
            uiHelper.highlightSelectedButton(btn);
        });
    }

    // ====== Confirmaciones (UI/Navegación) ======
    @FXML
    private void confirmarTiempoDeFoco() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }
        try {
            // Pre-configurar el tiempo de estudio, sin arrancar (arranca en Lección)
            if (pomodoroTimer != null) {
                pomodoroTimer.setSecondsLeft(minutosSeleccionados * 60);
                pomodoroTimer.pause();
            }
            // Ir a pantalla de elección de descanso
            navigator.goTo("/views/PomodoroDescanso.fxml", "Descanso", controllerFactory, confirmarButton);
        } catch (Exception ex) {
            uiHelper.showError("Error", ex.getMessage());
        }
    }

    @FXML
    private void confirmarTiempoDeDescanso() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo de descanso.");
            return;
        }
        try {
            if (pomodoroTimer != null) pomodoroTimer.pause(); // en principal no corre
            // ⚠️ Usa root como nodo de referencia para que Navigacion tenga un Node válido
            navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, root);
        } catch (Exception ex) {
            uiHelper.showError("Error", ex.getMessage());
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

    // ====== Atajo de teclado (demo) ======
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
    @FXML private void goHome()         { navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn); }
    @FXML private void goForum()        { uiHelper.showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements() { uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()      { uiHelper.showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }
}
