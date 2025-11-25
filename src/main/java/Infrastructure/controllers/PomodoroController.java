package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.PomodoroTimer;
import Application.services.SesionPomodoroService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PomodoroController {

    // ===== Dependencias =====
    private final SesionPomodoroService sesionPomodoroService;
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ===== UI helpers =====
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ===== FXML =====
    @FXML
    private AnchorPane root;
    @FXML
    private Label timerLabel;

    // Barra inferior
    @FXML
    private Button homeBtn, forumBtn, achievementsBtn, profileBtn, tiendaBtn, iguluBtn;

    // Selección de foco
    @FXML
    private Button btn25min, btn30min, btn45min, btn60min;

    // Confirmar
    @FXML
    private Button confirmarButton;

    // Estado
    private int minutosSeleccionados = 0;
    private final List<Button> botonesTiempo = new ArrayList<>();

    // ===== Constructores =====
    public PomodoroController(SesionPomodoroService sesionPomodoroService, PomodoroTimer pomodoroTimer) {
        this.sesionPomodoroService = sesionPomodoroService;
        this.pomodoroTimer = pomodoroTimer;
    }

    public PomodoroController() {
        this.sesionPomodoroService = AppServices.getSesionPomodoroService();
        this.pomodoroTimer = AppServices.getPomodoroTimer();
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== Ciclo de vida =====
    @FXML
    public void initialize() {
        if (pomodoroTimer != null) {
            try {
                pomodoroTimer.pause();
            } catch (Exception ignore) {
            }
        }
        configurarVistaPomodoro();
        configurarAtajoTeclado();
    }

    // ===== Vista Foco =====
    private void configurarVistaPomodoro() {
        setupTimeButton(btn25min, 25);
        setupTimeButton(btn30min, 30);
        setupTimeButton(btn45min, 45);
        setupTimeButton(btn60min, 60);

        if (confirmarButton != null) {
            confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
        }
    }

    private void setupTimeButton(Button btn, int minutes) {
        if (btn == null)
            return;
        botonesTiempo.add(btn);
        btn.setOnAction(e -> {
            minutosSeleccionados = minutes;
            // reset estilos
            for (Button b : botonesTiempo) {
                b.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
            }
            // seleccionado
            btn.setStyle(
                    "-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3;");
        });
    }

    @FXML
    private void confirmarTiempoDeFoco() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }

        PomodoroTimer pomodoroTimer = AppServices.getPomodoroTimer();
        if (pomodoroTimer != null) {
            pomodoroTimer.setSecondsLeft(minutosSeleccionados * 60);
            pomodoroTimer.start(); // ✅ Iniciar el conteo real
        }

        navigator.goTo("/views/PomodoroDescanso.fxml", "Descanso", controllerFactory, confirmarButton);
    }

    // ===== Navegación inferior =====
    @FXML
    private void goHome() {
        navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn);
    }

    @FXML
    private void goForum() {
        try {
            navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, forumBtn);
        } catch (Exception e) {
            uiHelper.showError("Error al navegar al foro", e.getMessage());
        }
    }

    @FXML
    private void goAchievements() {
        uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada.");
    }

    @FXML
    private void goProfile() {
        try {
            navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, profileBtn);
        } catch (Exception e) {
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, tiendaBtn);
    }

    // ===== Atajo demo =====
    private void configurarAtajoTeclado() {
        if (timerLabel == null)
            return;
        timerLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                    if ("F12".equals(ev.getCode().toString())) {
                        navigator.goTo("/views/PomodoroTiempoFinalizado.fxml", "¡Tiempo terminado!", controllerFactory,
                                timerLabel);
                        ev.consume();
                    }
                });
            }
        });
    }
}
