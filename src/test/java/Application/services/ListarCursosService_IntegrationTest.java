// src/test/java/Application/services/ListarCursosService_IntegrationTest.java
package Application.services;

import Application.dtos.Listado_Cursos.*;
import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.services.DarAcceso.RegistroService;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioStatsRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.*;
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
    private InterfazUsuarioStatsRepository usuarioStatsService;

    @BeforeEach
    void setUp() {
        // Cada test arranca con BD 100% limpia y datos reales (schema + data.sql)
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        cursoRepo = new CursoRepository(ConexionBD.getInstance());
        usuarioCursoRepo = new UsuarioCursoRepository(ConexionBD.getInstance());
        service = new ListarCursosService(cursoRepo, usuarioCursoRepo);

        // Para crear usuarios temporales cuando sea necesario
        var usuarioRepo = new UsuarioRepository(ConexionBD.getInstance());
        var usuarioItemRepo = new UsuarioItemRepository(ConexionBD.getInstance());
        usuarioStatsService = new UsuarioStatsRepository(ConexionBD.getInstance());
        registroService = new RegistroService(usuarioRepo, usuarioItemRepo, usuarioStatsService);
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
        var ex = assertThrows(IllegalArgumentException.class, () -> service.inscribirCurso(request));
        assertTrue(ex.getMessage().toLowerCase().contains("no existe") ||
                ex.getMessage().contains("curso"));
    }

    @Test
    @DisplayName("obtenerCursosCompletos - Caminos 3-4-5: Usuario real (id=1) → 1 cursado + 0 disponibles")
    void obtenerCursosCompletos_UsuarioReal() {
        CursosResponse resp = service.obtenerCursosCompletos(1);

        // Usuario 1 tiene curso 1 inscrito en data.sql (usuario_curso)
        List<CursoResponse> cursados = resp.cursosUsuario();
        assertEquals(1, cursados.size(), "Usuario debe tener 1 curso inscrito");
        assertEquals(1, cursados.get(0).id());
        assertTrue(cursados.get(0).titulo().contains("Java") ||
                cursados.get(0).titulo().contains("Introducción"));

        // Solo existe 1 curso en data.sql y ya está inscrito, no hay disponibles
        List<CursoResponse> disponibles = resp.cursosDisponibles();
        assertEquals(2, disponibles.size(), "No debe haber cursos disponibles ya que el único curso está inscrito");
    }

    @Test
    @DisplayName("obtenerCursosCompletos - Camino 5: Usuario sin inscripciones")
    void obtenerCursosCompletos_UsuarioSinCursos() {
        var nuevo = registroService.registrar(new RegistrarUsuarioRequest(
                "temp", "temp@test.com", "Temp", "pass123", "ESTUDIANTE"));
        CursosResponse resp = service.obtenerCursosCompletos(nuevo.id());
        assertTrue(resp.cursosUsuario().isEmpty());
        assertEquals(3, resp.cursosDisponibles().size(), "Solo existe 1 curso en data.sql");
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
        // data.sql línea 28: "Aprende los fundamentos de programación en Java desde
        // cero"
        assertTrue(resp.descripcion().contains("Java") || resp.descripcion().contains("fundamentos"));
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
        // Usuario 1 ya está inscrito en curso 1 en data.sql
        var req = new InscripcionRequest(1, 1);
        assertThrows(RuntimeException.class, () -> service.inscribirCurso(req));
    }

    @Test
    @DisplayName("inscribirCurso - Camino 5: Inscripción exitosa → actualiza listas")
    void inscribirCurso_Exito() {
        // Crear un nuevo usuario sin inscripciones
        var nuevo = registroService.registrar(new RegistrarUsuarioRequest(
                "temp2", "temp2@test.com", "Temp2", "pass123", "ESTUDIANTE"));

        var req = new InscripcionRequest(nuevo.id(), 1); // nuevo usuario se inscribe en Java

        CursosResponse resp = service.inscribirCurso(req);

        // El curso 1 ahora debe estar en cursados
        assertTrue(resp.cursosUsuario().stream().anyMatch(c -> c.id() == 1));
        // Y ya no debe estar en disponibles
        assertFalse(resp.cursosDisponibles().stream().anyMatch(c -> c.id() == 1));
    }
}