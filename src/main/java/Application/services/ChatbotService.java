// Application/services/ChatbotService.java
package Application.services;

import Application.dtos.chatbot.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatbotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // Historial de mensajes como lista de ChatMessageResponse para DTOs
    private List<ChatMessageResponse> conversationHistory = new ArrayList<>();

    public ChatbotService() {
        // Mensaje inicial del sistema
        ChatMessageResponse systemMsg = new ChatMessageResponse(
                "Eres Stella, una asistente útil y amable. Habla en español.",
                LocalDateTime.now(),
                "system");
        conversationHistory.add(systemMsg);
    }

    /**
     * Obtiene una respuesta del chatbot basada en el mensaje del usuario.
     *
     * @param request El mensaje del usuario encapsulado en un ChatMessageRequest.
     * @return Un ChatMessageResponse con la respuesta del chatbot y metadatos.
     */
    public ChatMessageResponse obtenerRespuesta(ChatMessageRequest request) {
        try {
            // Añadir el mensaje del usuario al historial
            ChatMessageResponse userMsg = new ChatMessageResponse(
                    request.content(),
                    LocalDateTime.now(),
                    "user");
            conversationHistory.add(userMsg);

            // Convertir el historial a formato JSON para la API
            JSONArray messagesArray = new JSONArray();
            for (ChatMessageResponse msg : conversationHistory) {
                JSONObject jsonMsg = new JSONObject()
                        .put("role", msg.role())
                        .put("content", msg.content());
                messagesArray.put(jsonMsg);
            }

            // Crear cuerpo de la solicitud
            JSONObject body = new JSONObject()
                    .put("model", "mistralai/mistral-7b-instruct")
                    .put("messages", messagesArray);

            // Construir la solicitud HTTP
            HttpRequest requestHttp = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getApiKey())
                    .header("HTTP-Referer", "http://localhost")
                    .header("X-Title", "Chatbot Stella")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            
            HttpClient client = createHttpClient();
            HttpResponse<String> response = client.send(requestHttp, HttpResponse.BodyHandlers.ofString());

            // Check for non-200 status codes
            if (response.statusCode() != 200) {
                logger.warn("Chatbot API returned non-200 status: {}", response.statusCode());
                return new ChatMessageResponse(
                        "Error de conexión: Recibido código " + response.statusCode(),
                        LocalDateTime.now(),
                        "assistant");
            }

            // Parsear respuesta JSON
            JSONObject json = new JSONObject(response.body());
            JSONArray choices = json.getJSONArray("choices");

            if (choices.length() > 0) {
                String respuesta = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()
                        .replaceAll("^[^a-zA-Z0-9¿¡]+", "");

                // Añadir respuesta del modelo al historial
                ChatMessageResponse assistantMsg = new ChatMessageResponse(
                        respuesta,
                        LocalDateTime.now(),
                        "assistant");
                conversationHistory.add(assistantMsg);

                return assistantMsg;
            }

            return new ChatMessageResponse(
                    "No se obtuvo respuesta del modelo.",
                    LocalDateTime.now(),
                    "assistant");
        } catch (IOException | InterruptedException e) {
            logger.error("Error de conexión al comunicarse con la API del chatbot", e);
            return new ChatMessageResponse(
                    "Error de conexión: " + e.getMessage(),
                    LocalDateTime.now(),
                    "assistant");
        } catch (Exception e) {
            logger.error("Error inesperado al procesar la respuesta del chatbot", e);
            return new ChatMessageResponse(
                    "Error al procesar la respuesta: " + e.getMessage(),
                    LocalDateTime.now(),
                    "assistant");
        }
    }

    /**
     * Obtiene el historial completo de la conversación.
     *
     * @return Un ChatConversation con la lista de mensajes intercambiados.
     */
    public ChatConversation obtenerHistorial() {
        return new ChatConversation(new ArrayList<>(conversationHistory));
    }

    /**
     * Limpia el historial de la conversación, reiniciando con el mensaje del
     * sistema.
     */
    public void limpiarHistorial() {
        conversationHistory = new ArrayList<>();
        ChatMessageResponse systemMsg = new ChatMessageResponse(
                "Eres Stella, una asistente útil y amable. Habla en español.",
                LocalDateTime.now(),
                "system");
        conversationHistory.add(systemMsg);
    }

    protected HttpClient createHttpClient() {
        return HttpClient.newHttpClient();
    }

    protected String getApiKey() {
        return API_KEY;
    }

    protected String getApiUrl() {
        return API_URL;
    }
}