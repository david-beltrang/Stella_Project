package main.java.Infrastructure.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class PrincipalController {

    @FXML
    private Button loginButton, registroButton, startPomodoroButton;

    @FXML
    private void goToLogin() {
        cambiarEscena("Login.fxml", loginButton);
    }

    @FXML
    private void goToRegistro() {
        cambiarEscena("Registro.fxml", registroButton);
    }

    @FXML
    private void goToPomodoro() {
        cambiarEscena("Pomodoro.fxml", startPomodoroButton);
    }

    private void cambiarEscena(String fxml, Button botonReferencia) {
        try {
            Parent vista = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/stellaa/" + fxml)));
            Stage stage = (Stage) botonReferencia.getScene().getWindow();
            Scene escena = new Scene(vista);
            stage.setScene(escena);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar " + fxml);
        }
    }
}
