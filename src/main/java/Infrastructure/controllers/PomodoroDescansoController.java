package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.PomodoroTimer;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PomodoroDescansoController {

    // ===== Dependencias =====
    private final PomodoroTimer pomodoroTimer;
    private Function<Class<?>, Object> controllerFactory;

    // ===== UI helpers =====
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    // ===== FXML =====
    @FXML private AnchorPane root;

    // Barra inferior
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn, tiendaBtn, iguluBtn;

    // Selección descanso
    @FXML private Button btn3min, btn5min, btn8min, btn10min;

    // Confirmar
    @FXML private Button confirmarButton;

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
            try { pomodoroTimer.pause(); } catch (Exception ignore) {}
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
        if (btn == null) return;
        botonesTiempo.add(btn);
        btn.setOnAction(e -> {
            minutosSeleccionados = minutes;
            // reset estilos
            for (Button b : botonesTiempo) {
                b.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
            }
            // seleccionado
            btn.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3;");
            System.out.println("[DEBUG] Descanso seleccionado: " + minutosSeleccionados + " min");
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
            pomodoroTimer.setSecondsLeft(minutosSeleccionados * 60);
            pomodoroTimer.start(); // ✅ Asegura inicio real
        }

        navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, confirmarButton);
    }


    // ===== Navegación inferior =====
    @FXML private void goHome()         { navigator.goTo("/views/Principal.fxml", "Principal", controllerFactory, homeBtn); }
    @FXML private void goForum()        { uiHelper.showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements() { uiHelper.showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()      { uiHelper.showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }
    @FXML private void goTienda()       { navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, tiendaBtn); }
}
