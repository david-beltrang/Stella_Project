// src/test/java/Application/services/ChatbotService_IntegrationTest.java
package Application.services;

import Application.dtos.chatbot.*;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ChatbotService - Tests de integración REAL con OpenRouter (API key real)")
class ChatbotService_IntegrationTest {

    private ChatbotService service;

    @BeforeEach
    void setUp() {
        service = new ChatbotService();
        service.limpiarHistorial();
    }

    // ========================================================================
    // 1. obtenerRespuesta() → Complejidad ciclomática = 5
    // ========================================================================
    @Test
    @DisplayName("obtenerRespuesta - Camino feliz: recibe respuesta válida y coherente de Stella")
    void obtenerRespuesta_CaminoFeliz_RespuestaValida() {
        ChatMessageRequest request = new ChatMessageRequest("Hola Stella, ¿qué día es hoy?");

        ChatMessageResponse respuesta = service.obtenerRespuesta(request);

        // Solo verificamos que no sea mensaje de error
        assertFalse(respuesta.content().contains("Error de conexión"));
        assertFalse(respuesta.content().contains("Error al procesar"));
        assertFalse(respuesta.content().contains("No se obtuvo respuesta"));

        // Verificamos que sea una respuesta razonable
        assertFalse(respuesta.content().isBlank());
        assertTrue(respuesta.content().length() > 10); // Respuesta con sentido
        assertEquals("assistant", respuesta.role());
        assertNotNull(respuesta.timestamp());

        // Historial debe tener 3 mensajes
        assertEquals(3, service.obtenerHistorial().messages().size());
    }

    @Test
    @DisplayName("obtenerRespuesta - API key inválida → error 401 Unauthorized")
    void obtenerRespuesta_ApiKeyInvalida_ErrorAutenticacion() {
        // Creamos un servicio temporal con API key obviamente inválida
        ChatbotService serviceMal = new ChatbotService() {
            private static final String API_KEY = "sk-or-v1-esto-es-una-clave-falsa-123456789";
            //Se utiliza una subclase anónima
            @Override
            protected String getApiKey() {
                return API_KEY;
            }
        };

        ChatMessageRequest request = new ChatMessageRequest("Test");

        ChatMessageResponse respuesta = serviceMal.obtenerRespuesta(request);

        assertTrue(
                respuesta.content().contains("Error de conexión") ||
                        respuesta.content().contains("401") ||
                        respuesta.content().contains("Unauthorized") ||
                        respuesta.content().contains("invalid") ||
                        respuesta.content().contains("authentication"),
                "Debe indicar error de autenticación. Respuesta: " + respuesta.content()
        );
    }

    @Test
    @DisplayName("obtenerRespuesta - Sin internet o API caída → IOException")
    void obtenerRespuesta_SinConexion_ErrorRed() {
        // Forzamos una URL que no existe
        ChatbotService serviceSinRed = new ChatbotService() {
            private static final String API_URL = "http://localhost:9999/no-existe";
            //Se sobreescribe el método de la clase creando una subclase anínima para cambiar el comportamiento
            //solo una vez del servicio
            @Override
            protected String getApiUrl() {
                return API_URL;
            }
        };

        ChatMessageResponse respuesta = serviceSinRed.obtenerRespuesta(new ChatMessageRequest("Hola"));

        assertTrue(respuesta.content().contains("Error de conexión"));
    }

    // ========================================================================
    // 2. obtenerHistorial() → Complejidad ciclomática = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerHistorial - Devuelve historial completo y copia segura")
    void obtenerHistorial_RetornaHistorialCorrecto() {
        service.obtenerRespuesta(new ChatMessageRequest("Hola"));

        ChatConversation historial = service.obtenerHistorial();

        assertEquals(3, historial.messages().size());
        assertEquals("system", historial.messages().get(0).role());
        assertTrue(historial.messages().get(0).content().contains("Stella"));
        assertEquals("user", historial.messages().get(1).role());
        assertEquals("assistant", historial.messages().get(2).role());

        // Modificar el historial devuelto no afecta al interno
        historial.messages().clear();
        assertEquals(3, service.obtenerHistorial().messages().size());
    }

    // ========================================================================
    // 3. limpiarHistorial() → CC = 1
    // ========================================================================
    @Test
    @DisplayName("limpiarHistorial - Reinicia conversación correctamente")
    void limpiarHistorial_ReiniciaCorrectamente() {
        service.obtenerRespuesta(new ChatMessageRequest("Pregunta 1"));
        service.obtenerRespuesta(new ChatMessageRequest("Pregunta 2"));

        assertTrue(service.obtenerHistorial().messages().size() >= 4);

        service.limpiarHistorial();

        ChatConversation nuevo = service.obtenerHistorial();
        assertEquals(1, nuevo.messages().size());
        assertEquals("system", nuevo.messages().get(0).role());
        assertTrue(nuevo.messages().get(0).content().contains("Eres Stella"));
    }
}