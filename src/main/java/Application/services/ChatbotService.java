package Application.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatbotService {

    private static final String API_KEY = "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";
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    // Aquí guardamos el historial de mensajes
    private JSONArray historial = new JSONArray();

    public ChatbotService() {
        // Mensaje inicial del sistema (puedes personalizarlo)
        JSONObject systemMsg = new JSONObject()
                .put("role", "system")
                .put("content", "Eres Stella, una asistente útil y amable. Habla en español.");
        historial.put(systemMsg);
    }

    public String obtenerRespuesta(String mensajeUsuario) {
        try {
            // Añadir el mensaje del usuario al historial
            JSONObject userMsg = new JSONObject()
                    .put("role", "user")
                    .put("content", mensajeUsuario);
            historial.put(userMsg);

            // Crear cuerpo de la solicitud con todo el historial
            JSONObject body = new JSONObject()
                    .put("model", "mistralai/mistral-7b-instruct")
                    .put("messages", historial);

            // Construir la solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("HTTP-Referer", "http://localhost")
                    .header("X-Title", "Chatbot Stella")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            // Enviar la solicitud
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parsear respuesta JSON
            JSONObject json = new JSONObject(response.body());
            JSONArray choices = json.getJSONArray("choices");

            if (choices.length() > 0) {
                String respuesta = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();

                // Añadir respuesta del modelo al historial
                JSONObject assistantMsg = new JSONObject()
                        .put("role", "assistant")
                        .put("content", respuesta);
                historial.put(assistantMsg);

                // Limpiar tags extra
                respuesta = respuesta.replaceAll("^[^a-zA-Z0-9¿¡]+", "");
                return respuesta;
            }

            return "No se obtuvo respuesta del modelo.";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error de conexión: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al procesar la respuesta: " + e.getMessage();
        }
    }

    // Método para limpiar el historial cuando termine la conversación
    public void limpiarHistorial() {
        historial = new JSONArray();
        JSONObject systemMsg = new JSONObject()
                .put("role", "system")
                .put("content", "Eres Stella, una asistente útil y amable. Habla en español.");
        historial.put(systemMsg);
    }
}