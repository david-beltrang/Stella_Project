// src/test/java/Application/services/DarAcceso/LoginServiceIntegrationTest.java
package Application.services;

import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Application.services.DarAcceso.LoginService;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("LoginService - Pruebas de integración (Complejidad Ciclomatica = 3)")
class LoginServiceIntegrationTest {

    private LoginService loginService;
    private InterfazUsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        // limpia todo y carga data.sql
        var initializer = new H2DataBaseInitializer(ConexionBD.getInstance());
        initializer.initialize(); // DROP TABLE + CREATE + INSERT datos de prueba (incluido test@estudio.com)

        // 2. Crear repositorio y servicio frescos
        usuarioRepository = new UsuarioRepository(ConexionBD.getInstance());
        loginService = new LoginService(usuarioRepository);
    }
    // ========================================================================
    // login(LoginRequest request) → Complejidad ciclomática = 3
    // ========================================================================

    @Test
    @DisplayName("Camino 1: Correo no existe → IllegalArgumentException")
    void login_CorreoInexistente_LanzaExcepcion() {
        var request = new LoginRequest("noexiste@dominio.com", "cualquier");

        var exception = assertThrows(IllegalArgumentException.class, () -> loginService.login(request));

        assertEquals("Correo no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Camino 2: Contraseña incorrecta → IllegalArgumentException")
    void login_ContrasenaIncorrecta_LanzaExcepcion() {
        var request = new LoginRequest("test@estudio.com", "contraseñaMala123");

        var exception = assertThrows(IllegalArgumentException.class, () -> loginService.login(request));

        assertEquals("Contraseña incorrecta", exception.getMessage());
    }

    @Test
    @DisplayName("Camino 3: Login exitoso con datos de prueba → devuelve UsuarioResponse con ID")
    void login_CredencialesCorrectas_RetornaUsuarioResponse() {
        var request = new LoginRequest("test@estudio.com", "pass123");

        UsuarioResponse response = loginService.login(request);

        assertNotNull(response);
        assertEquals(1, response.id()); // el usuario de data.sql tiene id = 1
        assertEquals("JuanPa", response.username());
        assertEquals("test@estudio.com", response.correo());
        assertEquals("Juan Pérez", response.nombre());
        assertEquals("ESTUDIANTE", response.tipo());
    }
}