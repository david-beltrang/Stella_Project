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
        // BD limpia + todos los datos reales
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
    @DisplayName("obtenerQuizPorSeccion - Sección 1 (real) → devuelve quiz con 3 preguntas y opciones")
    void obtenerQuizPorSeccion_SeccionConQuiz_RetornaPruebaCompleta() {
        PruebaResponse quiz = service.obtenerQuizPorSeccion(1);

        assertEquals("Quiz - Fundamentos de Java", quiz.titulo());
        assertEquals("FINAL", quiz.tipo());
        assertEquals(3, quiz.preguntas().size());

        // Pregunta 1
        PreguntaResponse p1 = quiz.preguntas().get(0);
        assertEquals("¿Qué significa JDK?", p1.enunciado());
        assertEquals(4, p1.opciones().size());
        assertEquals("Java Development Kit", p1.opciones().get(0).texto()); // es_correcta = true
        assertTrue(p1.opciones().get(0).texto().contains("Java Development Kit"));

        // Pregunta 2
        PreguntaResponse p2 = quiz.preguntas().get(1);
        assertEquals("¿Cuál es la extensión de un archivo de código fuente Java?", p2.enunciado());
        assertEquals(".java", p2.opciones().get(0).texto()); // correcta
    }

    @Test
    @DisplayName("obtenerQuizPorSeccion - Sección sin prueba → lanza RuntimeException")
    void obtenerQuizPorSeccion_SinPrueba_LanzaExcepcion() {
        // Sección 2 y 3 tienen quiz en data.sql
        // Usaremos sección 99 que no existe
        var ex = assertThrows(RuntimeException.class, () -> service.obtenerQuizPorSeccion(99));
        assertTrue(ex.getMessage().contains("No hay prueba para esta sección"));
    }

    // ========================================================================
    // 2. crearIntento(IntentoRequest request) → Complejidad Ciclomática = 6
    // ========================================================================
    @Test
    @DisplayName("crearIntento - 100% aciertos → puntaje 100 y mensaje ¡Perfecto!")
    void crearIntento_TodoCorrecto() {
        // Con AUTO_INCREMENT, los IDs de opciones son secuenciales:
        // Pregunta 1 (ID=1): opciones 1-4 (opción 1 es correcta: "Java Development
        // Kit")
        // Pregunta 2 (ID=2): opciones 5-8 (opción 5 es correcta: ".java")
        // Pregunta 3 (ID=3): opciones 9-12 (opción 9 es correcta: "main()")
        var request = new IntentoRequest(
                1, // usuarioId (test@estudio.com)
                1, // pruebaId
                List.of(
                        new RespuestaRequest(1, 1), // pregunta 1 → opción 1 (Java Development Kit)
                        new RespuestaRequest(2, 5), // pregunta 2 → opción 5 (.java)
                        new RespuestaRequest(3, 9) // pregunta 3 → opción 9 (main())
                ));

        IntentoResponse resp = service.crearIntento(request);

        assertEquals(100.0, resp.puntaje());
        assertEquals(3, resp.totalPreguntas());
        assertEquals(3, resp.aciertos());
        assertEquals("¡Perfecto!", resp.mensaje());
        assertTrue(resp.id() > 0);
    }

    @Test
    @DisplayName("crearIntento - 66% aciertos → puntaje 66.6")
    void crearIntento_DosTerciosCorrecto() {
        var request = new IntentoRequest(
                1,
                1,
                List.of(
                        new RespuestaRequest(1, 1), // correcta (Java Development Kit)
                        new RespuestaRequest(2, 6), // incorrecta (.class)
                        new RespuestaRequest(3, 9) // correcta (main())
                ));

        IntentoResponse resp = service.crearIntento(request);
        // 2 de 3 = 66.666...
        assertEquals(66.666, resp.puntaje(), 0.01);
        assertEquals(2, resp.aciertos());
        assertEquals("Buen intento", resp.mensaje());
    }

    @Test
    @DisplayName("crearIntento - 0% aciertos → puntaje 0")
    void crearIntento_TodoMal() {
        var request = new IntentoRequest(
                1,
                1,
                List.of(
                        new RespuestaRequest(1, 2), // incorrecta (Java Design Kit)
                        new RespuestaRequest(2, 6), // incorrecta (.class)
                        new RespuestaRequest(3, 10) // incorrecta (start())
                ));

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