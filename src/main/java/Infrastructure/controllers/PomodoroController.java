package main.java.Infrastructure.controllers;

import main.java.Application.services.PomodoroTimer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PomodoroController {

    @FXML
    private Label timerLabel;

    @FXML
    private Button startButton;

    private final PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();
    private boolean alreadyBound = false;

    @FXML
    public void initialize() {
        // Vincula la propiedad del temporizador con el label (una sola vez)
        if (!alreadyBound) {
            pomodoroTimer.secondsLeftProperty().addListener((obs, oldVal, newVal) -> {
                int seconds = newVal.intValue();
                int minutes = seconds / 60;
                int secs = seconds % 60;
                timerLabel.setText(String.format("%02d:%02d", minutes, secs));

                if (seconds == 0) {
                    playEndAnimation();
                }
            });
            alreadyBound = true;
        }

        // Muestra el tiempo actual al cargar
        int seconds = pomodoroTimer.getSecondsLeft();
        timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    @FXML
    private void startTimer() {
        pomodoroTimer.start();
        startButton.setText("Pausar");
        startButton.setOnAction(e -> pauseTimer());
    }

    @FXML
    private void pauseTimer() {
        pomodoroTimer.pause();
        startButton.setText("Reanudar");
        startButton.setOnAction(e -> startTimer());
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/stellaa/Principal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al volver a la vista principal.");
        }
    }

    /** Animación de parpadeo al terminar */
    private void playEndAnimation() {
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), timerLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(6);
        fade.setAutoReverse(true);
        fade.play();
    }
}
