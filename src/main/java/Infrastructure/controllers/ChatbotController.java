package Infrastructure.controllers;

import Application.services.ChatbotService;
import Application.dtos.chatbot.ChatMessageRequest;
import Application.dtos.chatbot.ChatMessageResponse;
import Application.dtos.chatbot.ChatConversation;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.util.function.Function;

public class ChatbotController {

    // ========= Dependencias =======
    private final ChatbotService chatbotService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ========= FXML =========
    @FXML private Pane root;
    @FXML private TextArea txtHistorial;
    @FXML private TextField txtMensajeUsuario;
    @FXML private Button btnEnviar;
    @FXML private Button btnLimpiar;

    // ========= Constructor =========
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        txtHistorial.setEditable(false);
        refrescarHistorial();
    }

    // ========= Lógica chatbot =========
    @FXML
    private void onEnviarMensaje() {
        String mensaje = txtMensajeUsuario.getText();
        if (mensaje == null || mensaje.isBlank()) {
            return;
        }

        try {
            ChatMessageRequest request = new ChatMessageRequest(mensaje);
            ChatMessageResponse respuesta = chatbotService.obtenerRespuesta(request);

            txtMensajeUsuario.clear();
            refrescarHistorial();
        } catch (Exception e) {
            uiHelper.mostrarError("Error en el chatbot", e.getMessage());
        }
    }

    private void refrescarHistorial() {
        try {
            ChatConversation conv = chatbotService.obtenerHistorial();
            StringBuilder sb = new StringBuilder();
            conv.mensajes().forEach(msg -> {
                String prefix = switch (msg.rol()) {
                    case "user" -> "Tú: ";
                    case "assistant" -> "Stella: ";
                    case "system" -> "[Sistema]: ";
                    default -> "";
                };
                sb.append(prefix).append(msg.contenido()).append("\n\n");
            });
            txtHistorial.setText(sb.toString());
            txtHistorial.positionCaret(txtHistorial.getText().length());
        } catch (Exception e) {
            uiHelper.mostrarError("Error cargando historial", e.getMessage());
        }
    }

    @FXML
    private void onLimpiarHistorial() {
        chatbotService.limpiarHistorial();
        refrescarHistorial();
    }

    @FXML
    private void onVolverAPrincipal() {
        navigator.irAPrincipal(root, controllerFactory);
    }
}
