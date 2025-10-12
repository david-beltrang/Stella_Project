package com.example.stellaa;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;

public class PomodoroController {

    @FXML
    private Label timerLabel;

    @FXML
    private Button startButton;

    private final PomodoroTimer pomodoroTimer = PomodoroTimer.getInstance();

    private boolean alreadyBound = false;

    @FXML
    public void initialize() {
        // Evitar volver a hacer binding si ya se hizo una vez
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

        // Mostrar el valor actual al entrar (no reinicia)
        int seconds = pomodoroTimer.secondsLeftProperty().get();
        timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    @FXML
    private void startTimer() {
        pomodoroTimer.start();
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("principal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Animación al terminar el Pomodoro
    private void playEndAnimation() {
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), timerLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setCycleCount(6);
        fade.setAutoReverse(true);
        fade.play();
    }
}
