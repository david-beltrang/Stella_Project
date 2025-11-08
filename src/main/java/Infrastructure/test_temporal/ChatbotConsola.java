// Infrastructure/test_temporal/ChatbotConsola.java
package Infrastructure.test_temporal;

import Application.dtos.Chatbot.ChatMessageRequest;
import Application.dtos.Chatbot.ChatMessageResponse;
import Application.services.ChatbotService;
import java.util.Scanner;

public class ChatbotConsola {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ChatbotService chatbot = new ChatbotService();

        System.out.println("¡Bienvenido al Chatbot Stella! Escribe 'salir' para terminar.");

        while (true) {
            System.out.print("Tú: ");
            String mensajeUsuario = scanner.nextLine().trim();

            if (mensajeUsuario.equalsIgnoreCase("salir")) {
                chatbot.limpiarHistorial();
                System.out.println("¡Adiós! Gracias por charlar con Stella.");
                break;
            }

            // Crear solicitud DTO y obtener respuesta
            ChatMessageRequest request = new ChatMessageRequest(mensajeUsuario);
            ChatMessageResponse response = chatbot.obtenerRespuesta(request);

            // Mostrar solo el contenido limpio del modelo
            System.out.println("Stella: " + response.content());
        }

        scanner.close();
    }
}