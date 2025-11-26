package Infrastructure.controllers;

import Application.dtos.chatbot.ChatConversation;
import Application.dtos.chatbot.ChatMessageRequest;
import Application.dtos.chatbot.ChatMessageResponse;
import Application.services.ChatbotService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
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

import java.util.function.Function;

public class ChatbotController {

    // ========= Dependencias =========
    private final ChatbotService chatbotService;
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ========= FXML =========
    @FXML private Pane popupRoot;
    @FXML private VBox chatContainer;
    @FXML private TextArea messageInput;
    @FXML private ScrollPane chatScrollPane;
    @FXML private Button sendButton;

    // ========= Constructor =========
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }
    public ChatbotController() {
        this.chatbotService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
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
        if (mensaje.isEmpty()) return;

        try {
            // Mostrar mensaje del usuario en pantalla
            agregarMensajeUsuario(mensaje);

            // Enviar al servicio
            ChatMessageRequest request = new ChatMessageRequest(mensaje);
            chatbotService.obtenerRespuesta(request);

            messageInput.clear();
            cargarHistorial();  // refresca con la respuesta del bot
        } catch (Exception e) {
            uiHelper.showError("Error en el chatbot", e.getMessage());
        }
    }

    // ========= Mostrar historial =========
    private void cargarHistorial() {
        try {
            chatContainer.getChildren().clear();

            ChatConversation conv = chatbotService.obtenerHistorial();
            for (ChatMessageResponse msg : conv.messages()) {
                switch (msg.role()) {
                    case "user" -> agregarMensajeUsuario(msg.content());
                    case "assistant" -> agregarMensajeBot(msg.content());
                    case "system" -> agregarMensajeSistema(msg.content());
                }
            }

            chatScrollPane.layout();
            chatScrollPane.setVvalue(1.0); // siempre al final
        } catch (Exception e) {
            uiHelper.showError("Error cargando historial", e.getMessage());
        }
    }

    // ========= Render visual de mensajes =========
    private void agregarMensajeUsuario(String texto) {
        HBox userBox = new HBox();
        userBox.setStyle("-fx-alignment: center-right;");

        Label label = new Label(texto);
        label.setStyle("-fx-background-color: #4DA3FF; -fx-text-fill: white; -fx-padding: 10 15; -fx-background-radius: 15;");
        label.setFont(Font.font(16));

        ImageView userIcon = new ImageView(new Image(getClass().getResourceAsStream("/Image/General/usuario.png")));
        userIcon.setFitHeight(40);
        userIcon.setFitWidth(40);

        userBox.getChildren().addAll(label, userIcon);
        userBox.setSpacing(10);

        chatContainer.getChildren().add(userBox);
    }

    private void agregarMensajeBot(String texto) {
        HBox botBox = new HBox();
        botBox.setStyle("-fx-alignment: center-left;");

        ImageView botIcon = new ImageView(new Image(getClass().getResourceAsStream("/Image/General/ChatBot.png")));
        botIcon.setFitHeight(40);
        botIcon.setFitWidth(40);

        Label label = new Label(texto);
        label.setStyle("-fx-background-color: #E8E8E8; -fx-text-fill: #333; -fx-padding: 10 15; -fx-background-radius: 15;");
        label.setFont(Font.font(16));

        botBox.getChildren().addAll(botIcon, label);
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
        rootPane.getScene().getWindow().hide();
    }
}
