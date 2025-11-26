// src/test/java/Application/services/PruebaService_IntegrationTest.java
package Application.services;

import Application.dtos.Prueba.*;
import Domain.repositoriesInterfaces.*;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.*;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PruebaService - Tests de integración con datos reales (CC: 1 + 6)")
class PruebaService_IntegrationTest {

    private PruebaService service;
    private InterfazPruebaRepository pruebaRepo;
    private InterfazIntentoRepository intentoRepo;
    private InterfazOpcionRepository opcionRepo;

    @BeforeEach
    void setUp() {
        // BD limpia + tods los datos reales
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        pruebaRepo = new PruebaRepository(ConexionBD.getInstance());
        intentoRepo = new IntentoRepository(ConexionBD.getInstance());
        opcionRepo = new OpcionRepository(ConexionBD.getInstance());

        service = new PruebaService(pruebaRepo, intentoRepo, opcionRepo);
    }

    // ========================================================================
    // 1. obtenerQuizPorSeccion(int seccionId) → Complejidad ciclomática = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerQuizPorSeccion - Sección 1 (real) → devuelve quiz con 2 preguntas y opciones")
    void obtenerQuizPorSeccion_SeccionConQuiz_RetornaPruebaCompleta() {
        PruebaResponse quiz = service.obtenerQuizPorSeccion(1);

        assertEquals("Quiz: Tipos de datos en C++", quiz.titulo());
        assertEquals("SECCIONAL", quiz.tipo());
        assertEquals(2, quiz.preguntas().size());

        // Pregunta 1
        PreguntaResponse p1 = quiz.preguntas().get(0);
        assertEquals("¿Cómo se declara una variable int en C++?", p1.enunciado());
        assertEquals(3, p1.opciones().size());
        assertEquals("int x;", p1.opciones().get(0).texto()); // es_correcta = true
        assertTrue(p1.opciones().get(0).texto().contains("int x;"));

        // Pregunta 2
        PreguntaResponse p2 = quiz.preguntas().get(1);
        assertEquals("¿Qué tipo de dato se usa para texto?", p2.enunciado());
        assertEquals("string", p2.opciones().get(0).texto()); // correcta
    }

    @Test
    @DisplayName("obtenerQuizPorSeccion - Sección sin prueba → lanza RuntimeException")
    void obtenerQuizPorSeccion_SinPrueba_LanzaExcepcion() {
        var ex = assertThrows(RuntimeException.class, () -> service.obtenerQuizPorSeccion(2));
        assertTrue(ex.getMessage().contains("No hay prueba para esta sección"));
    }

    // ========================================================================
    // 2. crearIntento(IntentoRequest request) → Complejidad Ciclomática = 6
    // ========================================================================
    @Test
    @DisplayName("crearIntento - 100% aciertos → puntaje 100 y mensaje ¡Perfecto!")
    void crearIntento_TodoCorrecto() {
        var request = new IntentoRequest(
                1,  // usuarioId (test@estudio.com)
                1,  // pruebaId
                List.of(
                        new RespuestaRequest(1, 1),  // pregunta 1 → opción 1 (int x;) → correcta
                        new RespuestaRequest(2, 4)   // pregunta 2 → opción 4 (string) → correcta (asumiendo id=4)
                )
        );

        IntentoResponse resp = service.crearIntento(request);

        assertEquals(100.0, resp.puntaje());
        assertEquals(2, resp.totalPreguntas());
        assertEquals(2, resp.aciertos());
        assertEquals("¡Perfecto!", resp.mensaje());
        assertTrue(resp.id() > 0);
    }

    @Test
    @DisplayName("crearIntento - 50% aciertos → puntaje 50")
    void crearIntento_MitadCorrecto() {
        var request = new IntentoRequest(
                1,
                1,
                List.of(
                        new RespuestaRequest(1, 1),  // correcta
                        new RespuestaRequest(2, 5)   // incorrecta (int)
                )
        );

        IntentoResponse resp = service.crearIntento(request);
        assertEquals(50.0, resp.puntaje());
        assertEquals(1, resp.aciertos());
        assertEquals("Buen intento", resp.mensaje());
    }

    @Test
    @DisplayName("crearIntento - 0% aciertos → puntaje 0")
    void crearIntento_TodoMal() {
        var request = new IntentoRequest(
                1,
                1,
                List.of(
                        new RespuestaRequest(1, 2),  // incorrecta
                        new RespuestaRequest(2, 6)   // incorrecta
                )
        );

        IntentoResponse resp = service.crearIntento(request);
        assertEquals(0.0, resp.puntaje());
        assertEquals(0, resp.aciertos());
        assertEquals("Buen intento", resp.mensaje());
    }

    // camino con respuestas vacías
    @Test
    @DisplayName("crearIntento - sin respuestas → puntaje 0")
    void crearIntento_SinRespuestas() {
        var request = new IntentoRequest(1, 1, List.of());

        IntentoResponse resp = service.crearIntento(request);
        assertEquals(0.0, resp.puntaje());
        assertEquals(0, resp.totalPreguntas());
        assertEquals(0, resp.aciertos());
    }
}