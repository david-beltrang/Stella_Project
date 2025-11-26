// src/test/java/Application/services/ProgresoService_IntegrationTest.java
package Application.services;

import Application.dtos.progreso.*;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.ProgresoRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ProgresoService - Tests de integración reales (15 lecciones + redondeo 2 decimales)")
class ProgresoService_IntegrationTest {

    private ProgresoService service;
    private InterfazProgresoRepository progresoRepo;

    @BeforeEach
    void setUp() {
        // BD limpia + todos los datos reales
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        progresoRepo = new ProgresoRepository(ConexionBD.getInstance());
        service = new ProgresoService(progresoRepo);
    }
    //====================================================
    // 1. marcarCompletada() → Complejidad ciclomática = 3
    //=====================================================
    @Test
    @DisplayName("marcarCompletada - Lección EN_PROGRESO pasa a COMPLETADA (46.67% → 53.33%)")
    void marcarCompletada_LeccionEnProgreso_ActualizaEstado() {
        var request = new ProgresoRequest(1, 8); // lección 8 estaba EN_PROGRESO

        ProgresoResponse resp = service.marcarCompletada(request);

        assertEquals("COMPLETADA", resp.estado());
        assertEquals(1, resp.usuarioId());
        assertEquals(8, resp.leccionId());

        // Progreso sube de 46.67% → 53.33%
        var cursoResp = service.obtenerProgresoPorCurso(1, 1);
        assertEquals(53.33, cursoResp.porcentaje(), 0.001);
    }

    @Test
    @DisplayName("marcarCompletada - Lección ya COMPLETADA → no cambia (sigue 46.67%)")
    void marcarCompletada_YaCompletada_NoCambia() {
        var request = new ProgresoRequest(1, 1); // lección 1 ya completada

        ProgresoResponse resp = service.marcarCompletada(request);
        assertEquals("COMPLETADA", resp.estado());

        // Progreso NO cambia
        var cursoResp = service.obtenerProgresoPorCurso(1, 1);
        assertEquals(46.67, cursoResp.porcentaje(), 0.001);
    }

    @Test
    @DisplayName("marcarCompletada - Completa todo → llega a 100.0%")
    void marcarCompletada_CompletaTodo_Alcanza100() {
        // Marcamos las lecciones 8 a 15 como completadas
        for (int i = 8; i <= 15; i++) {
            service.marcarCompletada(new ProgresoRequest(1, i));
        }

        var resp = service.obtenerProgresoPorCurso(1, 1);
        assertEquals(100.0, resp.porcentaje(), 0.001);
    }

    //==========================================================================================
    // 2. obtenerProgresoPorCurso() → complejidad ciclomática = 1 pero verificación adicional
    //por business rules
    //=========================================================================================
    @Test
    @DisplayName("obtenerProgresoPorCurso - Estado inicial: 7 completadas → 46.67%")
    void obtenerProgresoPorCurso_Inicio_Correcto() {
        var resp = service.obtenerProgresoPorCurso(1, 1);
        assertEquals(1, resp.cursoId());
        assertEquals(46.67, resp.porcentaje(), 0.001);
    }

    @Test
    @DisplayName("obtenerProgresoPorCurso - Curso sin inscripción (Java id=2) → 0.0%")
    void obtenerProgresoPorCurso_SinInscripcion_Cero() {
        var resp = service.obtenerProgresoPorCurso(1, 2);
        assertEquals(0.0, resp.porcentaje(), 0.001);
    }

    @Test
    @DisplayName("obtenerProgresoPorCurso - Curso inexistente → 0.0%")
    void obtenerProgresoPorCurso_CursoInexistente_Cero() {
        var resp = service.obtenerProgresoPorCurso(1, 999);
        assertEquals(0.0, resp.porcentaje(), 0.001);
    }

    @Test
    @DisplayName("obtenerProgresoPorCurso - 10 completadas → 66.67% (redondeo correcto)")
    void obtenerProgresoPorCurso_RedondeoCorrecto() {
        // Marcamos 3 más para llegar a 10 completadas
        service.marcarCompletada(new ProgresoRequest(1, 8));
        service.marcarCompletada(new ProgresoRequest(1, 9));
        service.marcarCompletada(new ProgresoRequest(1, 10));

        var resp = service.obtenerProgresoPorCurso(1, 1);
        assertEquals(66.67, resp.porcentaje(), 0.001);
    }
}