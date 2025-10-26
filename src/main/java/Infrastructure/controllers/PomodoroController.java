package Infrastructure.controllers;

import Application.services.PomodoroTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.function.Function;

public class PomodoroController {

    // ====== CONFIG ======
    // Cambia a false cuando ya no quieras que el pomodoro se simule rápido
    private static final boolean DEBUG_FAST = true;

    // ====== INYECCIÓN DE DEPENDENCIAS ======
    private final PomodoroTimer pomodoroTimer;

    // Esta factory la inyecta ControllerControladores para que este
    // controlador pueda navegar sin crear controladores nuevos.
    private Function<Class<?>, Object> controllerFactory;

    // Setter para que el orquestador nos pase su controllerFactory global
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // Constructor recomendado (lo usa ControllerControladores)
    public PomodoroController(PomodoroTimer pomodoroTimer) {
        this.pomodoroTimer = pomodoroTimer;
    }

    // Constructor vacío opcional (fallback). Si no quieres fallback, lo puedes borrar.
    public PomodoroController() {
        this.pomodoroTimer = PomodoroTimer.getInstance();
    }

    // ====== NODOS FXML (pueden ser null en algunas pantallas si el FXML no los declara) ======
    @FXML private Button btn3min;
    @FXML private Button btn5min;
    @FXML private Button btn8min;
    @FXML private Button btn10min;
    @FXML private Button btn25min;
    @FXML private Button btn30min;
    @FXML private Button btn45min;
    @FXML private Button btn60min;

    private int minutosSeleccionados = 0;

    @FXML private Label  timerLabel;
    @FXML private Button startButton;
    @FXML private Button confirmarButton;

    // Navegación inferior
    @FXML private Button homeBtn, forumBtn, achievementsBtn, profileBtn;

    // Elementos del quiz de Lección 3 (solo existen en ciertos FXML)
    @FXML private RadioButton rbStart, rbMain, rbBegin, rbRun;
    @FXML private ToggleGroup q1Group;
    @FXML private Label lbResultadoL3;

    // Estado interno
    private boolean alreadyBound = false;
    private boolean finalShown = false; // evita abrir final más de una vez

    // ====== CICLO DE VIDA DEL CONTROLADOR ======
    @FXML
    public void initialize() {

        // (1) Atajo F12 para saltar a la pantalla de "tiempo finalizado"
        Platform.runLater(() -> {
            try {
                Stage st = null;
                if (homeBtn != null && homeBtn.getScene() != null) {
                    st = (Stage) homeBtn.getScene().getWindow();
                } else if (confirmarButton != null && confirmarButton.getScene() != null) {
                    st = (Stage) confirmarButton.getScene().getWindow();
                } else if (startButton != null && startButton.getScene() != null) {
                    st = (Stage) startButton.getScene().getWindow();
                }
                if (st != null && st.getScene() != null) {
                    st.getScene().addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
                        if (ev.getCode() == KeyCode.F12) {
                            showFinalScreen();
                            ev.consume();
                        }
                    });
                }
            } catch (Exception ignored) {}
        });

        // (2) Vincular el label al timer si existe en este FXML
        if (timerLabel != null && !alreadyBound) {
            pomodoroTimer
                    .secondsLeftProperty()
                    .addListener((obs, oldVal, newVal) -> {
                        int seconds = newVal.intValue();
                        int minutes = seconds / 60;
                        int secs    = seconds % 60;

                        Platform.runLater(() -> {
                            if (timerLabel != null) {
                                timerLabel.setText(String.format("%02d:%02d", minutes, secs));
                                if (seconds == 0) {
                                    playEndAnimation();
                                    if (!finalShown) {
                                        finalShown = true;
                                        pomodoroTimer.pause();
                                        showFinalScreen();
                                    }
                                }
                            }
                        });
                    });

            alreadyBound = true;

            // inicializar texto del timer
            int seconds = pomodoroTimer.getSecondsLeft();
            timerLabel.setText(String.format(
                    "%02d:%02d",
                    seconds / 60,
                    seconds % 60
            ));
        }

        // (3) Configurar listeners de botones si existen en este FXML

        if (startButton != null) {
            startButton.setOnAction(e -> startTimer());
        }

        if (confirmarButton != null) {
            confirmarButton.setOnAction(e -> confirmarTiempoDeFoco());
        }

        // tiempos de descanso
        if (btn3min != null)  btn3min.setOnAction(e -> seleccionarTiempo(3, btn3min));
        if (btn5min != null)  btn5min.setOnAction(e -> seleccionarTiempo(5, btn5min));
        if (btn8min != null)  btn8min.setOnAction(e -> seleccionarTiempo(8, btn8min));
        if (btn10min != null) btn10min.setOnAction(e -> seleccionarTiempo(10, btn10min));

        // tiempos de estudio
        if (btn25min != null) btn25min.setOnAction(e -> seleccionarTiempo(25, btn25min));
        if (btn30min != null) btn30min.setOnAction(e -> seleccionarTiempo(30, btn30min));
        if (btn45min != null) btn45min.setOnAction(e -> seleccionarTiempo(45, btn45min));
        if (btn60min != null) btn60min.setOnAction(e -> seleccionarTiempo(60, btn60min));
    }

    // ====== LÓGICA DE TIMER (UI-level) ======

    @FXML
    private void startTimer() {
        pomodoroTimer.start();

        // modo rápido para pruebas
        if (DEBUG_FAST) {
            int fakeSeconds = Math.max(5, (minutosSeleccionados == 0 ? 10 : minutosSeleccionados));
            PauseTransition pt = new PauseTransition(Duration.seconds(fakeSeconds));
            pt.setOnFinished(ev -> {
                if (!finalShown) {
                    finalShown = true;
                    pomodoroTimer.pause();
                    showFinalScreen();
                }
            });
            pt.play();
        }

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

    // ====== CONFIRMAR SESIÓN / CAMBIAR VISTA ======

    @FXML
    private void confirmarTiempoDeFoco() {
        if (minutosSeleccionados == 0) {
            showInfo("Selecciona un tiempo", "Debes elegir un tiempo antes de continuar.");
            return;
        }

        // Este bloque decide a dónde ir según desde qué pantalla estoy
        // (descanso o estudio). Lo mantenemos, pero navegamos con factory.
        String destino;
        if (btn3min != null || btn5min != null || btn8min != null || btn10min != null) {
            // Pantalla de descanso -> ir a CursoC++.fxml (tu pantalla de curso)
            destino = "/views/CursoC++.fxml";
        } else {
            // Pantalla de estudio -> ir a PomodoroDescanso.fxml
            destino = "/views/PomodoroDescanso.fxml";
        }

        cambiarVista(destino, "STELLA", confirmarButton != null ? confirmarButton : startButton);
    }

    private void seleccionarTiempo(int minutos, Button botonSeleccionado) {
        minutosSeleccionados = minutos;

        // resetear estilos
        if (btn25min != null) btn25min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn30min != null) btn30min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn45min != null) btn45min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        if (btn60min != null) btn60min.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");

        // resaltar botón actual
        botonSeleccionado.setStyle(
                "-fx-background-color: #00BFA6; -fx-text-fill: white; -fx-font-weight: bold;"
        );
    }

    // ====== NAVEGACIÓN GENERAL ======

    @FXML private void goBack()        { cambiarVista("/views/Principal.fxml", "STELLA - Principal", startButton); }
    @FXML private void goHome()        { cambiarVista("/views/Principal.fxml", "STELLA - Principal", homeBtn); }
    @FXML private void goForum()       { showInfo("Foro", "Pantalla de Foro aún no implementada."); }
    @FXML private void goAchievements(){ showInfo("Logros", "Pantalla de Logros aún no implementada."); }
    @FXML private void goProfile()     { showInfo("Perfil", "Pantalla de Perfil aún no implementada."); }

    @FXML private void goToLeccion1() { cambiarVista("/views/Leccion1.fxml", "Lección 1", homeBtn); }
    @FXML private void goToLeccion2() { cambiarVista("/views/Leccion2.fxml", "Lección 2", homeBtn); }
    @FXML private void goToLeccion3() { cambiarVista("/views/Leccion3.fxml", "Lección 3", homeBtn); }
    @FXML private void goToLeccion4() { cambiarVista("/views/Leccion4.fxml", "Lección 4", homeBtn); }
    @FXML private void goToLeccion5() { cambiarVista("/views/Leccion5.fxml", "Lección 5", homeBtn); }

    @FXML private void prevFrom1() { cambiarVista("/views/CursoC++.fxml", "Curso C++", homeBtn); }
    @FXML private void nextFrom1() { cambiarVista("/views/Leccion2.fxml", "Lección 2", homeBtn); }

    @FXML private void prevFrom2() { cambiarVista("/views/Leccion1.fxml", "Lección 1", homeBtn); }
    @FXML private void nextFrom2() { cambiarVista("/views/Leccion3.fxml", "Lección 3", homeBtn); }

    @FXML private void prevFrom3() { cambiarVista("/views/Leccion2.fxml", "Lección 2", homeBtn); }
    @FXML private void nextFrom3() { cambiarVista("/views/Leccion4.fxml", "Lección 4", homeBtn); }

    @FXML private void prevFrom4() { cambiarVista("/views/Leccion3.fxml", "Lección 3", homeBtn); }
    @FXML private void nextFrom4() { cambiarVista("/views/Leccion5.fxml", "Lección 5", homeBtn); }

    @FXML private void prevFrom5() { cambiarVista("/views/Leccion4.fxml", "Lección 4", homeBtn); }
    @FXML private void nextFrom5() { cambiarVista("/views/CursoC++.fxml", "Curso C++", homeBtn); }

    @FXML
    private void newStudySession() {
        cambiarVista("/views/Pomodoro.fxml", "Nueva sesión", homeBtn);
    }

    @FXML
    private void exitToHome() {
        cambiarVista("/views/Principal.fxml", "STELLA - Principal", homeBtn);
    }

    /** Pantalla mostrada automáticamente cuando el timer termina */
    private void showFinalScreen() {
        Button ref = homeBtn != null ? homeBtn
                : startButton != null ? startButton
                : confirmarButton;
        cambiarVista("/views/PomodoroTiempoFinalizado.fxml", "¡Tiempo terminado!", ref);
    }

    // ====== ANIMACIONES / FEEDBACK UI ======

    /** Parpadeo del timer al finalizar */
    private void playEndAnimation() {
        if (timerLabel == null) return;
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), timerLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(6);
        fade.setAutoReverse(true);
        fade.play();
    }

    /** Validación del quiz de Lección 3 */
    @FXML
    private void verificarLeccion3() {
        if (q1Group == null) return; // este FXML puede no tener el quiz
        var selected = q1Group.getSelectedToggle();
        if (selected == null) {
            if (lbResultadoL3 != null) {
                lbResultadoL3.setText("Selecciona una opción.");
                lbResultadoL3.setStyle("-fx-text-fill: #ffd166; -fx-font-weight: bold;");
            }
            return;
        }

        boolean ok = (selected == rbMain);

        if (lbResultadoL3 != null) {
            if (ok) {
                lbResultadoL3.setText("✅ ¡Correcto! La función principal es main().");
                lbResultadoL3.setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold;");
                if (rbStart != null) rbStart.setDisable(true);
                if (rbMain  != null) rbMain.setDisable(true);
                if (rbBegin != null) rbBegin.setDisable(true);
                if (rbRun   != null) rbRun.setDisable(true);
            } else {
                lbResultadoL3.setText("❌ Incorrecto. La correcta es main().");
                lbResultadoL3.setStyle("-fx-text-fill: #ff5252; -fx-font-weight: bold;");
            }
        }
    }

    // ====== HELPERS ======

    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Info");
        a.setHeaderText(title);
        a.setContentText(msg);
        a.showAndWait();
        a.close();
    }

    /**
     * Cambia la vista actual a otra escena FXML usando SIEMPRE la misma
     * controllerFactory global (inyectada por ControllerControladores).
     */
    private void cambiarVista(String fxmlPath, String tituloVentana, Button refButton) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(fxmlPath),
                            "No se encontró " + fxmlPath + " en el classpath"
                    )
            );

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent root = loader.load();

            // Determinar Stage (ventana actual)
            Stage stage;
            if (refButton != null && refButton.getScene() != null) {
                stage = (Stage) refButton.getScene().getWindow();
            } else if (timerLabel != null && timerLabel.getScene() != null) {
                stage = (Stage) timerLabel.getScene().getWindow();
            } else if (startButton != null && startButton.getScene() != null) {
                stage = (Stage) startButton.getScene().getWindow();
            } else if (confirmarButton != null && confirmarButton.getScene() != null) {
                stage = (Stage) confirmarButton.getScene().getWindow();
            } else {
                throw new IllegalStateException("No hay referencia para obtener el Stage");
            }

            stage.setScene(new Scene(root));
            stage.setTitle(tituloVentana);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Error", "No se pudo abrir " + fxmlPath + ": " + e.getMessage());
        }
    }
}
