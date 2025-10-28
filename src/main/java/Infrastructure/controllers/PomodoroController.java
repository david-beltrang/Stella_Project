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

import java.util.function.Function;

// Controlador unificado para las vistas Pomodoro.fxml, PomodoroDescanso.fxml y PomodoroTiempoFinalizado.fxml.
// Se encarga de manejar la lógica de interacción y la navegación entre las tres etapas del flujo Pomodoro:
// 1. Selección de tiempo de estudio
// 2. Descanso
// 3. Pantalla de tiempo finalizado
public class PomodoroController {

    // ====== Dependencias (inyectadas por ControllerControladores) ======
    private final SesionPomodoroService sesionPomodoroService;
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ====== Dependencias de interfaz ======
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ====== FXML ======
    @FXML private AnchorPane root;
    @FXML private Label timerLabel;

    // Botones genéricos para navegación inferior
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    // Botones de selección de tiempo
    @FXML private Button btn3min, btn5min, btn8min, btn10min;
    @FXML private Button btn25min, btn30min, btn45min, btn60min;

    // Botones de control de sesión
    @FXML private Button startButton, pauseButton, confirmarButton;

    // ====== Variables internas ======
    private int minutosSeleccionados;

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

    // ====== Setter requerido por ControllerControladores ======
    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ====== Ciclo de vida ======
    @FXML
    public void initialize() {
        if (root == null) return; // Evita errores si alguna vista no tiene el elemento raíz identificado.

        String fxmlName = root.getId() != null ? root.getId() : "";

        // Determina qué vista está activa según sus elementos.
        if (confirmarButton != null && (btn25min != null || btn30min != null)) {
            // === Vista Pomodoro principal ===
            configurarVistaPomodoro();
        } else if (confirmarButton != null && (btn3min != null || btn5min != null)) {
            // === Vista de descanso ===
            configurarVistaDescanso();
        } else if (root.lookup("#homeBtn") != null && root.lookup("#achievementsBtn") != null) {
            // === Vista de tiempo finalizado ===
            configurarVistaFinal();
        }
    }

    // ====== Configuración general ======
    private void configurarVistaPomodoro() {
        // Configura los botones de tiempo y el botón de confirmación de foco.
        setupTimeButtonsPomodoro();
        if (confirmarButton != null) confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
        atajoTeclado();
    }

    private void configurarVistaDescanso() {
        // Configura los botones de tiempo de descanso y el botón confirmar.
        setupTimeButtonsDescanso();
        if (confirmarButton != null) confirmarButton.setOnAction(e -> confirmarTiempoDeDescanso());
    }

    private void configurarVistaFinal() {
        // En esta vista se manejan los botones de nueva sesión y salir.
        Button newSessionBtn = (Button) root.lookup("#newStudySession");
        Button exitBtn = (Button) root.lookup("#exitToHome");

        if (newSessionBtn != null)
            newSessionBtn.setOnAction(e -> newStudySession());

        if (exitBtn != null)
            exitBtn.setOnAction(e -> exitToHome());
    }

    // ====== Configuración Pomodoro ======
    private void setupTimeButtonsPomodoro() {
        setupTimeButton(btn25min, 25);
        setupTimeButton(btn30min, 30);
        setupTimeButton(btn45min, 45);
        setupTimeButton(btn60min, 60);
    }

    // ====== Configuración Descanso ======
    private void setupTimeButtonsDescanso() {
        setupTimeButton(btn3min, 3);
        setupTimeButton(btn5min, 5);
        setupTimeButton(btn8min, 8);
        setupTimeButton(btn10min, 10);
    }

    // Método genérico para todos los botones de selección de tiempo.
    private void setupTimeButton(Button btn, int minutes) {
        if (btn != null) {
            btn.setOnAction(e -> {
                minutosSeleccionados = minutes;
                uiHelper.highlightSelectedButton(btn);
            });
        }
    }

    // ====== Eventos específicos ======

    // Confirmación del tiempo de estudio (va a la vista de descanso)
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

    // Confirmación del tiempo de descanso (va a la vista final)
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

    // Crea una nueva sesión de estudio desde la pantalla final
    @FXML
    private void newStudySession() {
        navigator.goTo("/views/Pomodoro.fxml", "Nueva sesión Pomodoro", controllerFactory, root);
    }

    // Sale al menú principal desde la pantalla final
    @FXML
    private void exitToHome() {
        navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, root);
    }

    // ====== Configuración de atajo de teclado ======
    private void atajoTeclado() {
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

