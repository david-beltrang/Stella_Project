package Infrastructure.controllers;

import Application.services.PomodoroTimer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class PomodoroController {

    @FXML private Button btn3min;
    @FXML private Button btn5min;
    @FXML private Button btn8min;
    @FXML private Button btn10min;
    @FXML private Button btn25min;
    @FXML private Button btn30min;
    @FXML private Button btn45min;
    @FXML private Button btn60min;

    private int minutosSeleccionados = 0;

    @FXML private Label  timerLabel;     // puede ser null si este FXML no lo define
    @FXML private Button startButton;    // idem
    @FXML private Button confirmarButton; // <-- para "CONFIRMAR TIEMPO DE FOCO"
    // --- NAV inferior ---
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    private final PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
    private boolean alreadyBound = false;


    @FXML
    public void initialize() {


        // Bind del temporizador solo si el label existe en este FXML
        if (timerLabel != null && !alreadyBound) {
            pomodoroTimer.secondsLeftProperty().addListener((obs, oldVal, newVal) -> {
                int seconds = newVal.intValue();
                int minutes = seconds / 60;
                int secs    = seconds % 60;

                Platform.runLater(() -> {
                    if (timerLabel != null) {
                        timerLabel.setText(String.format("%02d:%02d", minutes, secs));
                        if (seconds == 0) playEndAnimation();
                    }
                });
            });
            alreadyBound = true;

            int seconds = pomodoroTimer.getSecondsLeft();
            timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
        }

        // Botón de iniciar/pausar si existe
        if (startButton != null) {
            startButton.setOnAction(e -> startTimer());
        }

        // Botón "CONFIRMAR TIEMPO DE FOCO" si existe
        if (confirmarButton != null) {
            confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
        }

        // --- Botones de descanso ---
        if (btn3min != null)  btn3min.setOnAction(e -> seleccionarTiempo(3, btn3min));
        if (btn5min != null)  btn5min.setOnAction(e -> seleccionarTiempo(5, btn5min));
        if (btn8min != null)  btn8min.setOnAction(e -> seleccionarTiempo(8, btn8min));
        if (btn10min != null) btn10min.setOnAction(e -> seleccionarTiempo(10, btn10min));

// --- Botones de estudio ---
        if (btn25min != null) btn25min.setOnAction(e -> seleccionarTiempo(25, btn25min));
        if (btn30min != null) btn30min.setOnAction(e -> seleccionarTiempo(30, btn30min));
        if (btn45min != null) btn45min.setOnAction(e -> seleccionarTiempo(45, btn45min));
        if (btn60min != null) btn60min.setOnAction(e -> seleccionarTiempo(60, btn60min));

    }

    // --- Handlers de temporizador (si este FXML los usa) ---
    @FXML
    private void startTimer() {
        pomodoroTimer.start();
        if (startButton != null) {
            startButton.setText("Pausar");
            startButton.setOnAction(e -> pauseTimer());
        }
    }

    @FXML
    private void pauseTimer() {
        pomodoroTimer.pause();
        if (startButton != null) {
            startButton.setText("Reanudar");
            startButton.setOnAction(e -> startTimer());
        }
    }

    // --- Navegaciones ---
    /** Volver a Principal (si tienes un botón que lo use) */
    @FXML
    private void goBack() {
        // Usa cualquier botón presente para obtener el Stage sin NPE
        Button ref = confirmarButton != null ? confirmarButton :
                startButton     != null ? startButton     : null;
        gotoView("/views/Principal.fxml", ref);
    }

    /** Acción del botón CONFIRMAR TIEMPO DE FOCO */
    @FXML
    private void confirmarTiempoDeFoco() {
        if (minutosSeleccionados == 0) {
            showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }

        String destino;

        // Detecta si este FXML tiene los botones de descanso o estudio
        if (btn3min != null || btn5min != null || btn8min != null || btn10min != null) {
            destino = "/views/CursoC++.fxml"; // Si está en PomodoroDescanso
        } else {
            destino = "/views/PomodoroDescanso.fxml"; // Si está en Pomodoro principal
        }

        try {
            Parent vista = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(destino)));
            Stage stage = (Stage) confirmarButton.getScene().getWindow();
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Error", "No se pudo abrir la vista destino: " + e.getMessage());
        }
    }


    private void seleccionarTiempo(int minutos, Button boton) {
        minutosSeleccionados = minutos;

        // Resetear estilos de todos
        if (btn25min != null) btn25min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn30min != null) btn30min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn45min != null) btn45min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn60min != null) btn60min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");

        // Resaltar el seleccionado
        boton.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white; -fx-font-weight: bold;");
    }


    // Helper genérico de navegación
    private void gotoView(String fxmlPath, Button refButton) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource(fxmlPath),
                    "No se encontró " + fxmlPath + " en el classpath"
            ));
            // Si no hay botón de referencia, intento obtener el Stage de cualquier etiqueta que exista
            Stage stage;
            if (refButton != null) {
                stage = (Stage) refButton.getScene().getWindow();
            } else if (timerLabel != null) {
                stage = (Stage) timerLabel.getScene().getWindow();
            } else if (startButton != null) {
                stage = (Stage) startButton.getScene().getWindow();
            } else if (confirmarButton != null) {
                stage = (Stage) confirmarButton.getScene().getWindow();
            } else {
                throw new IllegalStateException("No hay referencia para obtener el Stage");
            }
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            // evita dialog extra; el stack trace en consola basta durante dev
        }
    }

    /** Animación de parpadeo cuando llega a 0 */
    private void playEndAnimation() {
        if (timerLabel == null) return;
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), timerLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(6);
        fade.setAutoReverse(true);
        fade.play();
    }

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
        a.close();
    }
    @FXML
    private void goHome() {
        gotoView("/views/Principal.fxml", homeBtn);
    }

    @FXML
    private void goForum() {
        // TODO: reemplazar cuando exista la vista del foro
        showInfo("Foro", "Pantalla de Foro aún no implementada.");
    }

    @FXML
    private void goAchievements() {
        // TODO: reemplazar cuando exista la vista de logros
        showInfo("Logros", "Pantalla de Logros aún no implementada.");
    }

    @FXML
    private void goProfile() {
        // TODO: reemplazar cuando exista la vista de perfil
        showInfo("Perfil", "Pantalla de Perfil aún no implementada.");
    }

}



