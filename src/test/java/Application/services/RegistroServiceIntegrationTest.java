// src/test/java/Application/services/DarAcceso/RegistroServiceIntegrationTest.java
package Application.services;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.RegistroService;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("RegistroService - Pruebas de integración (CC = 3)")
class RegistroServiceIntegrationTest {

    private RegistroService registroService;
    private InterfazUsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        //limpia todo y carga data.sql
        var initializer = new H2DataBaseInitializer(ConexionBD.getInstance());
        initializer.initialize(); // ← DROP + CREATE + INSERT data.sql (incluye test@estudio.com)

        usuarioRepository = new UsuarioRepository(ConexionBD.getInstance());
        registroService = new RegistroService(usuarioRepository);
    }
    // ========================================================================
    // 1. registrar(RegistrarUsuarioRequest request) → Complejidad ciclomática = 3
    // ========================================================================
    @Test
    @DisplayName("Camino 1: Registro exitoso de usuario nuevo → devuelve UsuarioResponse con ID")
    void registrar_NuevoUsuario_Exito() {
        var request = new RegistrarUsuarioRequest(
                "nuevouser2025",           // username nuevo
                "nuevo2025@test.com",      // correo nuevo
                "Nuevo Usuario",
                "passwordSegura123",
                "ESTUDIANTE"
        );

        UsuarioResponse response = registroService.registrar(request);

        assertNotNull(response);
        assertTrue(response.id() > 0);                    // ID generado por H2
        assertEquals("nuevouser2025", response.username());
        assertEquals("nuevo2025@test.com", response.correo());
        assertEquals("Nuevo Usuario", response.nombre());
        assertEquals("ESTUDIANTE", response.tipo());
    }

    @Test
    @DisplayName("Camino 2: Correo ya existente → IllegalArgumentException")
    void registrar_CorreoDuplicado_LanzaExcepcion() {
        // Intentamos registrar con el correo que YA existe en data.sql
        var request = new RegistrarUsuarioRequest(
                "otrousuario",
                "test@estudio.com",        // ← este correo ya está en data.sql
                "Otro Nombre",
                "pass123",
                "PROFESOR"
        );

        var exception = assertThrows(IllegalArgumentException.class, () ->
                registroService.registrar(request)
        );

        assertTrue(exception.getMessage().contains("ya está registrado")
                || exception.getMessage().contains("El correo ya está registrado"));
    }

    @Test
    @DisplayName("Camino 3: Username duplicado → SQLException → RuntimeException")
    void registrar_UsernameDuplicado_LanzaRuntimeException() {
        // Primero registramos un usuario con username "testestudio" el cual es el mismo que existe en data.sql
        var request1 = new RegistrarUsuarioRequest(
                "testestudio",             // username que ya existe en data.sql
                "unico2025@test.com",     // correo diferente para que no salte UsuarioYaExisteException
                "Nombre Cualquiera",
                "pass123",
                "ESTUDIANTE"
        );

        // Este intento lanza SQLException por UNIQUE violation en username
        // va al catch (Exception e) - RuntimeException
        var exception = assertThrows(RuntimeException.class, () ->
                registroService.registrar(request1)
        );

        assertTrue(exception.getMessage().contains("Error al registrar usuario"));
    }
}