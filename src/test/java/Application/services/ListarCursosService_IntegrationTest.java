// src/test/java/Application/services/ListarCursosService_IntegrationTest.java
package Application.services;

import Application.dtos.Listado_Cursos.*;
import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.services.DarAcceso.RegistroService;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.CursoRepository;
import Infrastructure.repositories.UsuarioCursoRepository;
import Infrastructure.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ListarCursosService - Tests de integración completos 13 caminos totales")
class ListarCursosService_IntegrationTest {

    private ListarCursosService service;
    private InterfazCursoRepository cursoRepo;
    private InterfazUsuarioCursoRepository usuarioCursoRepo;
    private RegistroService registroService;

    @BeforeEach
    void setUp() {
        // Cada test arranca con BD 100% limpia y datos reales (schema + data.sql)
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        cursoRepo = new CursoRepository(ConexionBD.getInstance());
        usuarioCursoRepo = new UsuarioCursoRepository(ConexionBD.getInstance());
        service = new ListarCursosService(cursoRepo, usuarioCursoRepo);

        // Para crear usuarios temporales cuando sea necesario
        registroService = new RegistroService(new UsuarioRepository(ConexionBD.getInstance()));
    }

    // ========================================================================
    // 1. obtenerCursosCompletos(Integer usuarioId) → Comlejidad ciclomatica = 5
    // ========================================================================
    @Test
    @DisplayName("obtenerCursosCompletos - Camino 1: usuarioId nulo → IllegalArgumentException")
    void obtenerCursosCompletos_UsuarioIdNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.obtenerCursosCompletos(null));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 2: Curso inexistente → IllegalArgumentException")
    void inscribirCurso_CursoInexistente_LanzaExcepcion() {
        var request = new InscripcionRequest(1, 999); // usuario 1 intenta inscribirse en curso que no existe
        var ex = assertThrows(IllegalArgumentException.class, () ->
                service.inscribirCurso(request)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("no existe") ||
                ex.getMessage().contains("curso"));
    }

    @Test
    @DisplayName("obtenerCursosCompletos - Caminos 3-4-5: Usuario real (id=1) → 1 cursado + 3 disponibles")
    void obtenerCursosCompletos_UsuarioReal() {
        CursosResponse resp = service.obtenerCursosCompletos(1);

        List<CursoResponse> cursados = resp.cursosUsuario();
        assertEquals(1, cursados.size());
        assertEquals("Curso de C++ basico", cursados.get(0).titulo());

        List<CursoResponse> disponibles = resp.cursosDisponibles();
        assertEquals(3, disponibles.size());
        assertTrue(disponibles.stream().anyMatch(c -> c.id() == 2 && c.titulo().contains("java")));
        assertTrue(disponibles.stream().anyMatch(c -> c.id() == 3 && c.titulo().contains("python")));
        assertTrue(disponibles.stream().anyMatch(c -> c.id() == 4 && c.titulo().contains("GO")));
    }

    @Test
    @DisplayName("obtenerCursosCompletos - Camino 5: Usuario sin inscripciones")
    void obtenerCursosCompletos_UsuarioSinCursos() {
        var nuevo = registroService.registrar(new RegistrarUsuarioRequest(
                "temp", "temp@test.com", "Temp", "pass123", "ESTUDIANTE"));
        CursosResponse resp = service.obtenerCursosCompletos(nuevo.id());
        assertTrue(resp.cursosUsuario().isEmpty());
        assertEquals(4, resp.cursosDisponibles().size());
    }

    // ========================================================================
    // 2. verDetalles(DetallesRequest request) → Complejidad ciclomatica = 3
    // ========================================================================
    @Test
    @DisplayName("verDetalles - Camino 1: request nulo → IllegalArgumentException")
    void verDetalles_RequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.verDetalles(null));
    }

    @Test
    @DisplayName("verDetalles - Camino 2: curso_id <= 0 → IllegalArgumentException")
    void verDetalles_IdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.verDetalles(new DetallesRequest(0)));
    }

    @Test
    @DisplayName("verDetalles - Camino 3: Curso real (id=1) → devuelve descripción")
    void verDetalles_CursoExistente() {
        DetallesResponse resp = service.verDetalles(new DetallesRequest(1));
        assertEquals("En este curso aprenderas a manejar variables", resp.descripcion());
    }

    @Test
    @DisplayName("verDetalles - Curso inexistente → devuelve mensaje por defecto")
    void verDetalles_CursoInexistente() {
        DetallesResponse resp = service.verDetalles(new DetallesRequest(999));
        assertEquals("El curso no tiene descripcion", resp.descripcion());
    }

    // ========================================================================
    // 3. inscribirCurso(InscripcionRequest request) → Complejidad ciclomática = 5
    // ========================================================================
    @Test
    @DisplayName("inscribirCurso - Camino 1: request nulo → IllegalArgumentException")
    void inscribirCurso_RequestNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.inscribirCurso(null));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 2: usuario_id <= 0 → IllegalArgumentException")
    void inscribirCurso_UsuarioIdInvalido() {
        var req = new InscripcionRequest(-1, 1);
        assertThrows(IllegalArgumentException.class, () -> service.inscribirCurso(req));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 3: curso_id <= 0 → IllegalArgumentException")
    void inscribirCurso_CursoIdInvalido() {
        var req = new InscripcionRequest(1, 0);
        assertThrows(IllegalArgumentException.class, () -> service.inscribirCurso(req));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 4: Inscripción ya existe → RuntimeException")
    void inscribirCurso_YaInscrito() {
        var req = new InscripcionRequest(1, 1); // usuario 1 ya está inscrito en curso 1
        assertThrows(RuntimeException.class, () -> service.inscribirCurso(req));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 5: Inscripción exitosa → actualiza listas")
    void inscribirCurso_Exito() {
        var req = new InscripcionRequest(1, 2); // usuario 1 se inscribe en Java

        CursosResponse resp = service.inscribirCurso(req);

        // El curso 2 ahora debe estar en cursados
        assertTrue(resp.cursosUsuario().stream().anyMatch(c -> c.id() == 2));
        // Y ya no debe estar en disponibles
        assertFalse(resp.cursosDisponibles().stream().anyMatch(c -> c.id() == 2));
    }
}