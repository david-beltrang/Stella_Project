package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.PomodoroTimer;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PomodoroDescansoController {
    private static final Logger logger = LoggerFactory.getLogger(PomodoroDescansoController.class);

    // ===== Dependencias =====
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ===== UI helpers =====
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ===== FXML =====
    @FXML
    private AnchorPane root;

    // Barra inferior
    @FXML
    private Button homeBtn, forumBtn, achievementsBtn, profileBtn, tiendaBtn, iguluBtn;

    // Selección descanso
    @FXML
    private Button btn3min, btn5min, btn8min, btn10min;

    // Confirmar
    @FXML
    private Button confirmarButton;

    // Estado
    private int minutosSeleccionados = 0;
    private final List<Button> botonesTiempo = new ArrayList<>();

    // ===== Constructores =====
    public PomodoroDescansoController(PomodoroTimer pomodoroTimer) {
        this.pomodoroTimer = pomodoroTimer;
    }

    public PomodoroDescansoController() {
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
        configurarVistaDescanso();
    }

    private void configurarVistaDescanso() {
        setupTimeButton(btn3min, 3);
        setupTimeButton(btn5min, 5);
        setupTimeButton(btn8min, 8);
        setupTimeButton(btn10min, 10);

        if (confirmarButton != null) {
            confirmarButton.setOnAction(e -> confirmarTiempoDeDescanso());
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
            logger.debug("Descanso seleccionado: {} min", minutosSeleccionados);
        });
    }

    @FXML
    private void confirmarTiempoDeDescanso() {
        if (minutosSeleccionados == 0) {
            uiHelper.showInfo("Selecciona un tiempo", "Debes elegir un tiempo de descanso.");
            return;
        }

        PomodoroTimer pomodoroTimer = AppServices.getPomodoroTimer();
        if (pomodoroTimer != null) {
            pomodoroTimer.setBreakTimeSeconds(minutosSeleccionados * 60);
            pomodoroTimer.start(); // Inicia el conteo del tiempo de estudio
        }

        try {
            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, confirmarButton);
        } catch (Exception e) {
            logger.error("Error al navegar a Principal desde Pomodoro", e);
            uiHelper.showError("Error de navegación", "No se pudo volver a la pantalla principal.");
        }
    }

    // ===== Navegación inferior =====
    @FXML
    private void goHome() {
        try {
            navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn);
        } catch (Exception e) {
            logger.error("Error al navegar a Home desde Pomodoro", e);
        }
    }

    @FXML
    private void goForum() {
        try {
            navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, forumBtn);
        } catch (Exception e) {
            logger.error("Error al navegar al foro desde pomodoro descanso", e);
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
            logger.error("Error al navegar al perfil desde pomodoro descanso", e);
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, tiendaBtn);
        } catch (Exception e) {
            logger.error("Error al navegar a Tienda desde Pomodoro", e);
        }
    }
}