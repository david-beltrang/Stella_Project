// src/test/java/Application/services/SesionPomodoroService_IntegrationTest.java
package Application.services;

import Application.dtos.sesionEstudio.Pomodoro.*;
import Domain.repositoriesInterfaces.InterfazSesionEstudioRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.SesionEstudioRepository;
import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("SesionPomodoroService - Tests de integración reales (CC total: 5)")
class SesionPomodoroService_IntegrationTest {

    private SesionPomodoroService service;
    private InterfazSesionEstudioRepository repo;

    @BeforeEach
    void setUp() {
        // BD limpia + datos reales usuario id=1 existe
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        repo = new SesionEstudioRepository(ConexionBD.getInstance());
        service = new SesionPomodoroService(repo);
    }

    // ========================================================================
    // 1. iniciarSesion() → Complejidad ciclomática = 1 (camino feliz)
    // ========================================================================
    @Test
    @DisplayName("iniciarSesion - Crea nueva sesión correctamente")
    void iniciarSesion_CreaNuevaSesion() {
        var request = new IniciarSesionEstudioRequest(1, 25, 5);

        SesionEstudioResponse resp = service.iniciarSesion(request);

        assertNotNull(resp.id());
        assertTrue(resp.id() > 0);
        assertEquals(1, resp.usuarioId());
        assertEquals(25, resp.tiempoEstudio());
        assertEquals(5, resp.tiempoDescanso());
        assertNotNull(resp.fechaInicio());
        assertNull(resp.fechaFin()); // aún no finalizó
    }

    // ========================================================================
    // 2. obtenerEstadoSesion(int id) → Complejidad ciclomática = 2
    // ========================================================================
    @Test
    @DisplayName("obtenerEstadoSesion - Sesión existe → devuelve datos")
    void obtenerEstadoSesion_Existe_RetornaDatos() {
        // Primero iniciamos una sesión
        var inicio = new IniciarSesionEstudioRequest(1, 30, 10);
        SesionEstudioResponse creada = service.iniciarSesion(inicio);

        // Ahora la buscamos
        SesionEstudioResponse actual = service.obtenerEstadoSesion(creada.id());

        assertEquals(creada.id(), actual.id());
        assertEquals(30, actual.tiempoEstudio());
        assertNull(actual.fechaFin());
    }

    @Test
    @DisplayName("obtenerEstadoSesion - Sesión no existe → lanza IllegalArgumentException")
    void obtenerEstadoSesion_NoExiste_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> service.obtenerEstadoSesion(999));
    }

    // ========================================================================
    // 3. finalizarSesion(int id) → Complejidad ciclomática = 2
    // ========================================================================
    @Test
    @DisplayName("finalizarSesion - Sesión existe → marca fecha final y actualiza")
    void finalizarSesion_Existe_FinalizaCorrectamente() {
        // Iniciar sesión
        var request = new IniciarSesionEstudioRequest(1, 25, 5);
        SesionEstudioResponse iniciada = service.iniciarSesion(request);

        // Simular paso del tiempo para el test
        try { Thread.sleep(100); } catch (Exception ignored) {}

        // Finalizar
        SesionEstudioResponse finalizada = service.finalizarSesion(iniciada.id());

        assertNotNull(finalizada.fechaFin());
        assertTrue(finalizada.fechaFin().after(iniciada.fechaInicio()));
        assertEquals(25, finalizada.tiempoEstudio());
        assertEquals(5, finalizada.tiempoDescanso());
    }

    @Test
    @DisplayName("finalizarSesion - Sesión no existe → lanza IllegalArgumentException")
    void finalizarSesion_NoExiste_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> service.finalizarSesion(999));
    }

    // ========================================================================
    // 4. Flujo completo: iniciar → obtener → finalizar
    // ========================================================================
    @Test
    @DisplayName("Flujo completo Pomodoro - Todo funciona en cadena")
    void flujoCompleto_PomodoroFunciona() {
        // 1. Iniciar
        var inicio = new IniciarSesionEstudioRequest(1, 50, 10);
        SesionEstudioResponse sesion = service.iniciarSesion(inicio);

        int id = sesion.id();

        // 2. Consultar estado
        SesionEstudioResponse estado = service.obtenerEstadoSesion(id);
        assertNull(estado.fechaFin());

        // 3. Finalizar
        SesionEstudioResponse terminada = service.finalizarSesion(id);

        assertNotNull(terminada.fechaFin());
        assertTrue(terminada.fechaFin().after(terminada.fechaInicio()));
    }
}