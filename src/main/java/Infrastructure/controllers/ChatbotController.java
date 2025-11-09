package Infrastructure.controllers;

import Application.dtos.chatbot.ChatConversation;
import Application.dtos.chatbot.ChatMessageRequest;
import Application.dtos.chatbot.ChatMessageResponse;
import Application.services.ChatbotService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.function.Function;

public class ChatbotController {

    // ========= Dependencias =========
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

    // ========= Inicialización =========
    @FXML
    private void initialize() {
        if (txtHistorial != null) {
            txtHistorial.setEditable(false);
        }
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
            // Usa el método que tengas en tu servicio (ajusta el nombre si es diferente)
            chatbotService.obtenerRespuesta(request);

            txtMensajeUsuario.clear();
            refrescarHistorial();
        } catch (Exception e) {
            uiHelper.showError("Error en el chatbot", e.getMessage());
        }
    }

    private void refrescarHistorial() {
        try {
            ChatConversation conv = chatbotService.obtenerHistorial();

            StringBuilder sb = new StringBuilder();
            for (ChatMessageResponse msg : conv.messages()) {
                String prefix;
                switch (msg.role()) {
                    case "user" -> prefix = "Tú: ";
                    case "assistant" -> prefix = "Stella: ";
                    case "system" -> prefix = "[Sistema]: ";
                    default -> prefix = "";
                }

                sb.append(prefix)
                        .append(msg.content())
                        .append("\n(").append(msg.timestamp()).append(")")
                        .append("\n\n");
            }

            txtHistorial.setText(sb.toString());
            txtHistorial.positionCaret(txtHistorial.getText().length());
        } catch (Exception e) {
            uiHelper.showError("Error cargando historial", e.getMessage());
        }
    }

    @FXML
    private void onLimpiarHistorial() {
        try {
            chatbotService.limpiarHistorial();
            refrescarHistorial();
        } catch (Exception e) {
            uiHelper.showError("Error limpiando historial", e.getMessage());
        }
    }

    @FXML
    private void onVolverAPrincipal() {
        // Usamos tu Navigacion.goTo en vez de irAPrincipal (que no existe)
        navigator.goTo(
                "/fxml/Principal.fxml",   // ruta de tu principal, ajusta si usas otra
                "STELLA",                 // título de la ventana
                controllerFactory,        // factory global de controladores
                root                      // nodo origen para resolver el Stage
        );
    }
}
