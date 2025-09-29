package com.example.stellaa;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StellaController implements Initializable {

    public Button registroButton;
    @FXML
    private Button loginButton;          // Botón LOGIN en hello-view

    @FXML
    private Button backButton;           // Botón Volver en login-view

    @FXML
    private Button forgotPasswordButton; // Botón en login-view
    @FXML
    private Button sendRecoveryButton;   // Botón en recover-view
    @FXML
    private Button backToLoginButton;    // Botón en recover-view
    @FXML
    private TextField recoverEmailField; // Campo email en recover-view
    @FXML
    private Label recoveryMessage;       // Mensaje en recover-view
    
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Este método se ejecuta cuando se carga el FXML
    }

    // Método para ir a login (se llama desde hello-view)
    @FXML
    private void goToLogin() {
        try {
            // Cargar el FXML de login
            Parent loginView = FXMLLoader.load(getClass().getResource("Login.fxml"));

            // Obtener la ventana actual
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Cambiar la escena
            Scene loginScene = new Scene(loginView);
            stage.setScene(loginScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar Login.fxml");
        }
    }

    // Método para ir a login (se llama desde hello-view)
    @FXML
    private void goToRegistro() {
        try {
            // Cargar el FXML de login
            Parent registroView = FXMLLoader.load(getClass().getResource("Registro.fxml"));

            // Obtener la ventana actual
            Stage stage = (Stage) registroButton.getScene().getWindow();

            // Cambiar la escena
            Scene registroScene = new Scene(registroView);
            stage.setScene(registroScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar Registro.fxml");
        }
    }

    // Método para volver a hello (se llama desde Login)
    @FXML
    private void goBackToHello() {
        try {
            // Cargar el FXML de hello
            Parent helloView = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Obtener la ventana actual
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Cambiar la escena
            Scene helloScene = new Scene(helloView);
            stage.setScene(helloScene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar hello-view.fxml");
        }
    }

    // === NUEVOS MÉTODOS PARA RECUPERAR CONTRASEÑA ===

    // Método para ir a recuperar contraseña (desde login)
    @FXML
    private void goToRecoverPassword() {
        try {
            Parent recoverView = FXMLLoader.load(getClass().getResource("RecuperarContra.fxml"));
            Stage stage = (Stage) forgotPasswordButton.getScene().getWindow();
            Scene recoverScene = new Scene(recoverView);
            stage.setScene(recoverScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar RecuperarContra.fxml");
        }
    }

    // Método para volver al login (desde recuperar contraseña)
    @FXML
    private void goBackToLogin() {
        try {
            Parent loginView = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();
            Scene loginScene = new Scene(loginView);
            stage.setScene(loginScene);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar Login.fxml");
        }
    }

    // Método para enviar email de recuperación
    @FXML
    private void sendRecoveryEmail() {
        String email = recoverEmailField.getText();

        if (email == null || email.trim().isEmpty()) {
            showRecoveryMessage("Por favor ingresa tu email", "#FF6B6B");
            return;
        }

        if (!isValidEmail(email)) {
            showRecoveryMessage("Por favor ingresa un email válido", "#FF6B6B");
            return;
        }

        // Simulación de envío de email
        showRecoveryMessage("✓ Se ha enviado un enlace de recuperación a: " + email, "#90EE90");

        // Limpiar campo después de enviar
        recoverEmailField.clear();
    }

    // Método para validar formato de email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    // Método para mostrar mensajes de recuperación
    private void showRecoveryMessage(String message, String color) {
        if (recoveryMessage != null) {
            recoveryMessage.setText(message);
            recoveryMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 18px; -fx-font-weight: bold;");
            recoveryMessage.setVisible(true);
        }
    }

}

