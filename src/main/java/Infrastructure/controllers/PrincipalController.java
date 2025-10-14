package Infrastructure.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class PrincipalController {

    // Deben coincidir con los fx:id en Principal.fxml
    @FXML private Button pomodoroButton;
    @FXML private Button volverButton;

    @FXML
    private void goToPomodoro() {
        cambiarEscena("/views/Pomodoro.fxml", pomodoroButton);
    }

    @FXML
    private void goBackToLogin() {
        cambiarEscena("/views/Login.fxml", volverButton);
    }

    private void cambiarEscena(String fxmlPath, Button botonReferencia) {
        try {
            Parent vista = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource(fxmlPath),
                            "No se encontró " + fxmlPath + " en el classpath"
                    )
            );
            Stage stage = (Stage) botonReferencia.getScene().getWindow();
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();

        } catch (Exception e) {
            // Saca la causa real (útil cuando es LoadException)
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();

            e.printStackTrace(); // para ver el stack completo en la consola

            javafx.scene.control.Alert a =
                    new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("No pude cargar la vista");
            a.setContentText(
                    fxmlPath + "\n\nCausa: " + root.getClass().getSimpleName() +
                            "\nMensaje: " + (root.getMessage() == null ? "(sin mensaje)" : root.getMessage())
            );
            a.showAndWait();
        }
    }

    @FXML
    private void goHome() {
        // aquí puedes quedarte en Principal o recargar si quieres
    }

    @FXML
    private void goForum() {
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                "Pantalla de Foro aún no implementada.").showAndWait();
    }

    @FXML
    private void goAchievements() {
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                "Pantalla de Logros aún no implementada.").showAndWait();
    }

    @FXML
    private void goProfile() {
        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                "Pantalla de Perfil aún no implementada.").showAndWait();
    }


}


