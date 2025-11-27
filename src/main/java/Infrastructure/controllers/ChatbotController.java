package Infrastructure.controllers;

import Application.dtos.chatbot.ChatConversation;
import Application.dtos.chatbot.ChatMessageRequest;
import Application.dtos.chatbot.ChatMessageResponse;
import Application.services.ChatbotService;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.Label;

public class ChatbotController {

    // ========= Dependencias =========
    private final ChatbotService chatbotService;
    private final AyudaUI uiHelper = new AyudaUI();

    // ========= FXML =========
    @FXML
    private Pane popupRoot;
    @FXML
    private VBox chatContainer;
    @FXML
    private TextArea messageInput;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private Button sendButton;

    // ========= Constructor =========
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    public ChatbotController() {
        this.chatbotService = null;
    }

    // ========= Inicialización =========
    @FXML
    private void initialize() {
        if (messageInput != null) {
            messageInput.setWrapText(true);
        }
        cargarHistorial();
    }

    // ========= Enviar mensaje =========
    @FXML
    private void enviarMensaje() {
        String mensaje = messageInput.getText().trim();
        if (mensaje.isEmpty())
            return;

        // Mostrar mensaje del usuario en pantalla inmediatamente
        agregarMensajeUsuario(mensaje);
        messageInput.clear();

        // Deshabilitar input mientras procesa
        setInputEnabled(false);

        // Crear tarea en segundo plano
        javafx.concurrent.Task<Void> task = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
                ChatMessageRequest request = new ChatMessageRequest(mensaje);
                chatbotService.obtenerRespuesta(request);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            cargarHistorial(); // Refresca con la respuesta del bot
            setInputEnabled(true);
        });

        task.setOnFailed(e -> {
            Throwable error = task.getException();
            uiHelper.showError("Error en el chatbot", error.getMessage());
            setInputEnabled(true);
        });

        new Thread(task).start();
    }

    private void setInputEnabled(boolean enabled) {
        if (messageInput != null)
            messageInput.setDisable(!enabled);
        if (sendButton != null)
            sendButton.setDisable(!enabled);
    }

    // ========= Mostrar historial =========
    private void cargarHistorial() {
        try {
            javafx.application.Platform.runLater(() -> {
                chatContainer.getChildren().clear();
                try {
                    ChatConversation conv = chatbotService.obtenerHistorial();
                    for (ChatMessageResponse msg : conv.messages()) {
                        switch (msg.role()) {
                            case "user" -> agregarMensajeUsuario(msg.content());
                            case "assistant" -> agregarMensajeBot(msg.content());
                            case "system" -> agregarMensajeSistema(msg.content());
                        }
                    }
                    scrollToBottom();
                } catch (Exception e) {
                    uiHelper.showError("Error cargando historial", e.getMessage());
                }
            });
        } catch (Exception e) {
            // Ignorar errores de plataforma si ya se cerró
        }
    }

    private void scrollToBottom() {
        javafx.application.Platform.runLater(() -> {
            chatScrollPane.layout();
            chatScrollPane.setVvalue(1.0);
        });
    }

    // ========= Render visual de mensajes =========
    private void agregarMensajeUsuario(String texto) {
        HBox userBox = new HBox();
        userBox.setStyle("-fx-alignment: center-right;");

        javafx.scene.text.Text text = new javafx.scene.text.Text(texto);
        text.setFill(javafx.scene.paint.Color.WHITE);
        text.setFont(Font.font("System", 14));

        javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow(text);
        textFlow.setStyle("-fx-background-color: #4DA3FF; -fx-background-radius: 15; -fx-padding: 10 15;");
        textFlow.setMaxWidth(400); // Limitar ancho para forzar wrap

        ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("/Image/General/usuario.png")));
        userIcon.setFitHeight(40);
        userIcon.setFitWidth(40);

        userBox.getChildren().addAll(textFlow, userIcon);
        userBox.setSpacing(10);

        chatContainer.getChildren().add(userBox);
    }

    private void agregarMensajeBot(String texto) {
        HBox botBox = new HBox();
        botBox.setStyle("-fx-alignment: center-left;");

        ImageView botIcon = new ImageView(new Image(getClass().getResourceAsStream("/Image/General/ChatBot.png")));
        botIcon.setFitHeight(40);
        botIcon.setFitWidth(40);

        javafx.scene.text.Text text = new javafx.scene.text.Text(texto);
        text.setFill(javafx.scene.paint.Color.valueOf("#333333"));
        text.setFont(Font.font("System", 14));

        javafx.scene.text.TextFlow textFlow = new javafx.scene.text.TextFlow(text);
        textFlow.setStyle("-fx-background-color: #E8E8E8; -fx-background-radius: 15; -fx-padding: 10 15;");
        textFlow.setMaxWidth(400);

        botBox.getChildren().addAll(botIcon, textFlow);
        botBox.setSpacing(10);

        chatContainer.getChildren().add(botBox);
    }

    private void agregarMensajeSistema(String texto) {
        Label systemMsg = new Label("[Sistema] " + texto);
        systemMsg.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
        chatContainer.getChildren().add(systemMsg);
    }

    // ========= Cerrar popup =========
    @FXML
    private void cerrarPopup() {
        Pane rootPane = this.popupRoot;
        if (rootPane != null && rootPane.getScene() != null) {
            rootPane.getScene().getWindow().hide();
        }
    }
}
