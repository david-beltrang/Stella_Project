package Infrastructure.test_temporal;

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

            // Obtener respuesta del modelo
            String respuesta = chatbot.obtenerRespuesta(mensajeUsuario);

            // Mostrar solo el contenido limpio del modelo
            System.out.println("Stella: " + respuesta);
        }

        scanner.close();
    }
}