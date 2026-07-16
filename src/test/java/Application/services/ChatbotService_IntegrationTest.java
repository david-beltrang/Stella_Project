// src/test/java/Application/services/ChatbotService_IntegrationTest.java
package Application.services;

import Application.dtos.chatbot.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ChatbotService - Tests de integración (Simulados con Mockito)")
class ChatbotService_IntegrationTest {

    private ChatbotService service;

    @BeforeEach
    void setUp() {
        // Por defecto usamos el servicio real, pero los tests específicos usarán
        // subclases o mocks
        service = new ChatbotService();
        service.limpiarHistorial();
    }

    // ========================================================================
    // 1. obtenerRespuesta() → Complejidad ciclomática = 5
    // ========================================================================
    @Test
    @DisplayName("obtenerRespuesta - Camino feliz: recibe respuesta válida y coherente de Stella")
    void obtenerRespuesta_CaminoFeliz_RespuestaValida() throws IOException, InterruptedException {
        // Mock del HttpClient y HttpResponse
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        // Configurar comportamiento del mock
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("""
                {
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "Hoy es un buen día para aprender."
                      }
                    }
                  ]
                }
                """);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        // Inyectar el mock mediante subclase anónima
        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "fake-api-key";
            }
        };

        ChatMessageRequest request = new ChatMessageRequest("Hola Stella, ¿qué día es hoy?");
        ChatMessageResponse respuesta = serviceMock.obtenerRespuesta(request);

        // Solo verificamos que no sea mensaje de error
        assertFalse(respuesta.content().contains("Error de conexión"));
        assertFalse(respuesta.content().contains("Error al procesar"));
        assertFalse(respuesta.content().contains("No se obtuvo respuesta"));

        // Verificamos que sea una respuesta razonable
        assertFalse(respuesta.content().isBlank());
        assertTrue(respuesta.content().length() > 10); // Respuesta con sentido
        assertEquals("assistant", respuesta.role());
        assertNotNull(respuesta.timestamp());

        // Historial debe tener 3 mensajes (System + User + Assistant)
        assertEquals(3, serviceMock.obtenerHistorial().messages().size());
    }

    @Test
    @DisplayName("obtenerRespuesta - API key inválida → error 401 Unauthorized")
    void obtenerRespuesta_ApiKeyInvalida_ErrorAutenticacion() throws IOException, InterruptedException {
        // Mock del HttpClient y HttpResponse para simular 401
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(401);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "invalid-fake-key";
            }
        };

        ChatMessageRequest request = new ChatMessageRequest("Test");
        ChatMessageResponse respuesta = serviceMock.obtenerRespuesta(request);

        assertTrue(
                respuesta.content().contains("Error de conexión") ||
                        respuesta.content().contains("401") ||
                        respuesta.content().contains("Unauthorized"),
                "Debe indicar error de autenticación. Respuesta: " + respuesta.content());
    }

    @Test
    @DisplayName("obtenerRespuesta - Sin internet o API caída → IOException")
    void obtenerRespuesta_SinConexion_ErrorRed() throws IOException, InterruptedException {
        // Mock del HttpClient para lanzar excepción
        HttpClient mockClient = mock(HttpClient.class);

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Simulando error de red"));

        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "fake-api-key";
            }
        };

        ChatMessageResponse respuesta = serviceMock.obtenerRespuesta(new ChatMessageRequest("Hola"));

        assertTrue(respuesta.content().contains("Error de conexión"));
    }

    @Test
    @DisplayName("obtenerRespuesta - Error inesperado (RuntimeException) → Error al procesar")
    void obtenerRespuesta_ErrorInesperado_ExcepcionGenerica() throws IOException, InterruptedException {
        // Mock del HttpClient para lanzar RuntimeException
        HttpClient mockClient = mock(HttpClient.class);

        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("Error simulado inesperado"));

        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "fake-api-key";
            }
        };

        ChatMessageResponse respuesta = serviceMock.obtenerRespuesta(new ChatMessageRequest("Hola"));

        assertTrue(respuesta.content().contains("Error al procesar"));
        assertTrue(respuesta.content().contains("Error simulado inesperado"));
    }

    // ========================================================================
    // 2. obtenerHistorial() → Complejidad ciclomática = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerHistorial - Devuelve historial completo y copia segura")
    void obtenerHistorial_RetornaHistorialCorrecto() throws IOException, InterruptedException {
        // Usamos mock para poblar el historial sin llamar a la API real
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("""
                {
                  "choices": [
                    {
                      "message": {
                        "role": "assistant",
                        "content": "Respuesta simulada."
                      }
                    }
                  ]
                }
                """);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "fake-api-key";
            }
        };

        serviceMock.obtenerRespuesta(new ChatMessageRequest("Hola"));

        ChatConversation historial = serviceMock.obtenerHistorial();

        assertEquals(3, historial.messages().size());
        assertEquals("system", historial.messages().get(0).role());
        assertTrue(historial.messages().get(0).content().contains("Stella"));
        assertEquals("user", historial.messages().get(1).role());
        assertEquals("assistant", historial.messages().get(2).role());

        // Modificar el historial devuelto no afecta al interno
        historial.messages().clear();
        assertEquals(3, serviceMock.obtenerHistorial().messages().size());
    }

    // ========================================================================
    // 3. limpiarHistorial() → CC = 1
    // ========================================================================
    @Test
    @DisplayName("limpiarHistorial - Reinicia conversación correctamente")
    void limpiarHistorial_ReiniciaCorrectamente() throws IOException, InterruptedException {
        // Mock para evitar llamadas reales
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body())
                .thenReturn("{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"ok\"}}]}");
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);

        ChatbotService serviceMock = new ChatbotService() {
            @Override
            protected HttpClient createHttpClient() {
                return mockClient;
            }

            @Override
            protected String getApiKey() {
                return "fake-api-key";
            }
        };

        serviceMock.obtenerRespuesta(new ChatMessageRequest("Pregunta 1"));
        serviceMock.obtenerRespuesta(new ChatMessageRequest("Pregunta 2"));

        assertTrue(serviceMock.obtenerHistorial().messages().size() >= 4);

        serviceMock.limpiarHistorial();

        ChatConversation nuevo = serviceMock.obtenerHistorial();
        assertEquals(1, nuevo.messages().size());
        assertEquals("system", nuevo.messages().get(0).role());
        assertTrue(nuevo.messages().get(0).content().contains("Eres Stella"));
    }
}