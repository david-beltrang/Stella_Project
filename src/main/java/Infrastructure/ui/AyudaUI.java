package Infrastructure.ui;

import Application.services.PomodoroTimer;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * Helper que maneja únicamente animaciones y feedback visual.
 */
public class AyudaUI {

    public void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void highlightSelectedButton(Button btn) {
        if (btn == null) return;
        btn.setScaleX(1.05);
        btn.setScaleY(1.05);
        btn.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: white; -fx-border-width: 3;");
    }


    // Sincroniza el texto  con el temporizador Pomodoro
    // Cada vez que cambian los segundos restantes, se actualiza la etiqueta en formato mm:ss
    public void SincronizadorVisualPomodoro(Label label, PomodoroTimer timer) {
        timer.secondsLeftProperty().addListener((obs, o, n) -> {
            int sec = n.intValue();
            label.setText(String.format("%02d:%02d", sec / 60, sec % 60));
        });
    }

    // Actualiza el texto y color segun lo que pase en el quiz
    public void actualizacionVisualQuiz(Label lbl, String result) {
        lbl.setText(result);
        if (result.contains("Correcto")) {
            lbl.setTextFill(Color.LIMEGREEN);
        } else if (result.contains("Incorrecto")) {
            lbl.setTextFill(Color.RED);
        } else {
            lbl.setTextFill(Color.GOLD);
        }
    }
}