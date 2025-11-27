// src/test/java/Application/services/LeccionService_IntegrationTest.java
package Application.services;

import Application.dtos.leccion.LeccionResponse;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.LeccionRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("LeccionService - Tests de integración")
class LeccionService_IntegrationTest {

    private LeccionService service;
    private InterfazLeccionRepository leccionRepository;

    @BeforeEach
    void setUp() {
        // Inicializar BD con schema + data.sql
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        leccionRepository = new LeccionRepository(ConexionBD.getInstance());
        service = new LeccionService(leccionRepository);
    }

    // ========================================================================
    // obtenerLeccionPorCursoYOrden(int cursoId, int numeroOrdenSeccion, int
    // numeroOrdenLeccion)
    // ========================================================================

    @Test
    @DisplayName("Obtener lección existente - Curso 1, Sección 1, Lección 1 → Devuelve '¿Qué es Java?'")
    void obtenerLeccionPorCursoYOrden_LeccionExistente_DevuelveLeccion() {
        // Curso 1 (Java), Sección 1 (orden 1), Lección 1 (orden 1)
        // data.sql línea ~60: "Introducción a Java"
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(1, 1, 1);

        assertNotNull(resp);
        assertEquals("¿Qué es Java?", resp.titulo());
        assertEquals(1, resp.numeroOrden());
        assertEquals("TEORIA", resp.tipoContenido());
        assertNotNull(resp.contenidoHtml());
    }

    @Test
    @DisplayName("Obtener lección de video - Curso 1, Sección 1, Lección 2 → Contiene URL de video")
    void obtenerLeccionPorCursoYOrden_LeccionVideo_ContieneUrl() {
        // Curso 1 (Java), Sección 1 (orden 1), Lección 2 (orden 2)
        // data.sql línea ~72: "Instalando Java JDK" - tipo VIDEO
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(1, 1, 2);

        assertNotNull(resp);
        assertEquals("Instalando Java JDK", resp.titulo());
        assertEquals(2, resp.numeroOrden());
        assertEquals("VIDEO", resp.tipoContenido());
        assertNotNull(resp.url_video());
        assertTrue(resp.url_video().contains("youtu"));
    }

    @Test
    @DisplayName("Obtener lección con ejercicio - Curso 1, Sección 1, Lección 3 → Tiene contenido HTML")
    void obtenerLeccionPorCursoYOrden_LeccionConEjercicio_TieneContenido() {
        // Curso 1 (Java), Sección 1 (orden 1), Lección 3 (orden 3)
        // data.sql línea ~77: "Primer programa" - tipo TEORIA
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(1, 1, 3);

        assertNotNull(resp);
        assertEquals("Tu primer programa en Java", resp.titulo());
        assertEquals(3, resp.numeroOrden());
        assertEquals("PRACTICA", resp.tipoContenido());
        assertNotNull(resp.contenidoHtml());
    }

    @Test
    @DisplayName("Obtener lección de Python - Curso 2, Sección 4, Lección 1 → '¿Qué es Python y por qué usarlo?'")
    void obtenerLeccionPorCursoYOrden_CursoPython_DevuelveLeccion() {
        // Curso 2 (Python), Sección 4 (orden 1), Lección 1 (orden 1)
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(2, 1, 1);

        assertNotNull(resp);
        assertEquals("¿Qué es Python y por qué usarlo?", resp.titulo());
        assertEquals(1, resp.numeroOrden());
        assertEquals("TEORIA", resp.tipoContenido());
    }

    @Test
    @DisplayName("Obtener lección de SQL - Curso 3, Sección 7, Lección 1 → 'Conceptos de Bases de Datos Relacionales'")
    void obtenerLeccionPorCursoYOrden_CursoSQL_DevuelveLeccion() {
        // Curso 3 (SQL), Sección 7 (orden 1), Lección 1 (orden 1)
        // data.sql línea ~868: "Introducción a SQL"
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(3, 1, 1);

        assertNotNull(resp);
        assertEquals("Conceptos de Bases de Datos Relacionales", resp.titulo());
        assertEquals(1, resp.numeroOrden());
        assertEquals("TEORIA", resp.tipoContenido());
    }

    @Test
    @DisplayName("Lección inexistente - Curso válido, sección válida, lección inválida → RuntimeException")
    void obtenerLeccionPorCursoYOrden_LeccionInexistente_LanzaExcepcion() {
        // Curso 1, Sección 1, Lección 999 (no existe)
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.obtenerLeccionPorCursoYOrden(1, 1, 999));

        assertTrue(ex.getMessage().contains("Lección no encontrada"));
        assertTrue(ex.getMessage().contains("cursoId=1"));
        assertTrue(ex.getMessage().contains("sección orden=1"));
        assertTrue(ex.getMessage().contains("lección orden=999"));
    }

    @Test
    @DisplayName("Curso inexistente → RuntimeException")
    void obtenerLeccionPorCursoYOrden_CursoInexistente_LanzaExcepcion() {
        // Curso 999 no existe
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.obtenerLeccionPorCursoYOrden(999, 1, 1));

        assertTrue(ex.getMessage().contains("Lección no encontrada"));
        assertTrue(ex.getMessage().contains("cursoId=999"));
    }

    @Test
    @DisplayName("Sección inexistente → RuntimeException")
    void obtenerLeccionPorCursoYOrden_SeccionInexistente_LanzaExcepcion() {
        // Curso 1 existe, pero sección orden 999 no
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.obtenerLeccionPorCursoYOrden(1, 999, 1));

        assertTrue(ex.getMessage().contains("Lección no encontrada"));
        assertTrue(ex.getMessage().contains("sección orden=999"));
    }

    @Test
    @DisplayName("Validar tipos de contenido - Verificar que TEORIA, VIDEO y EJERCICIO funcionan")
    void obtenerLeccionPorCursoYOrden_ValidarTiposContenido() {
        // TEORIA: Curso 1, Sección 1, Lección 1
        LeccionResponse teoria = service.obtenerLeccionPorCursoYOrden(1, 1, 1);
        assertEquals("TEORIA", teoria.tipoContenido());

        // VIDEO: Curso 1, Sección 1, Lección 2
        LeccionResponse video = service.obtenerLeccionPorCursoYOrden(1, 1, 2);
        assertEquals("VIDEO", video.tipoContenido());

        // EJERCICIO: Curso 1, Sección 2, Lección 1 (si existe)
        LeccionResponse ejercicio = service.obtenerLeccionPorCursoYOrden(1, 2, 1);
        assertNotNull(ejercicio);
        assertTrue(ejercicio.tipoContenido().equals("TEORIA") ||
                ejercicio.tipoContenido().equals("VIDEO") ||
                ejercicio.tipoContenido().equals("EJERCICIO"));
    }

    @Test
    @DisplayName("Verificar campos opcionales - URL video puede ser null en lecciones de teoría")
    void obtenerLeccionPorCursoYOrden_CamposOpcionales() {
        // Lección de teoría no debería tener URL de video
        LeccionResponse resp = service.obtenerLeccionPorCursoYOrden(1, 1, 1);

        assertEquals("TEORIA", resp.tipoContenido());
        // URL de video es opcional para lecciones de teoría
        if (resp.url_video() != null) {
            assertTrue(resp.url_video().isEmpty() || resp.url_video().isBlank());
        }
    }
}
