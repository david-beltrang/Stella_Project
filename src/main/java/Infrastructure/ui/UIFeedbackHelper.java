package Infrastructure.ui;

import Application.services.PomodoroTimer;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * Helper que maneja únicamente animaciones y feedback visual.
 * Sin dependencias del backend.
 */
public class UIFeedbackHelper {

    public void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void highlightSelectedButton(Button btn) {
        btn.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white; -fx-font-weight: bold;");
    }

    public void bindTimerLabel(Label label, PomodoroTimer timer) {
        timer.secondsLeftProperty().addListener((obs, o, n) -> {
            int sec = n.intValue();
            label.setText(String.format("%02d:%02d", sec / 60, sec % 60));
        });
    }

    public void updateQuizFeedback(Label lbl, String result) {
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
//holapp