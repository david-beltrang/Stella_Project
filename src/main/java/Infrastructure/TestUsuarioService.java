package Infrastructure;

import Application.dtos.RegistrarUsuarioRequest;
import Application.dtos.LoginRequest;
import Application.dtos.ActualizarUsernameRequest;
import Application.dtos.UsuarioResponse;
import Application.services.DarAccesoService;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Infrastructure.repositories.UsuarioRepository;

/**
 * Clase para pruebas manuales rápidas del servicio UsuarioService.
 * Simula operaciones del frontend (registro, login, actualizar username) y verifica resultados en consola.
 * Usa una base de datos H2 en memoria para no dejar datos residuales.
 * Trabaja con la interfaz InterfazUsuarioRepository para mantener el desacoplamiento.
 */
public class TestUsuarioService {

    public static void main(String[] args) {
        // Instanciar la interfaz con la implementación concreta
        // INstanciar el servicio
        InterfazUsuarioRepository usuarioRepository = new UsuarioRepository();
        DarAccesoService accesoService = new DarAccesoService(usuarioRepository);

        // Prueba 1: Registrar un nuevo usuario
        System.out.println("=== Prueba 1: Registro ===");
        RegistrarUsuarioRequest registroRequest = new RegistrarUsuarioRequest(
                "user2", "test2@example.com", "TestUserDos", "pass456", "ESTUDIANTE"
        );
        try {
            UsuarioResponse response = accesoService.registrar(registroRequest);
            System.out.println("Registro exitoso: " + response);
            System.out.println("ID generado: " + response.id());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en registro: " + e.getMessage());
        }

        // Prueba 2: Registrar usuario con correo duplicado
        System.out.println("\n=== Prueba 2: Registro con correo duplicado ===");
        try {
            accesoService.registrar(registroRequest); // Mismo correo
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // Prueba 3: Login con credenciales correctas
        System.out.println("\n=== Prueba 3: Login correcto ===");
        LoginRequest loginRequest = new LoginRequest("test2@example.com", "pass456");
        try {
            UsuarioResponse response = accesoService.login(loginRequest);
            System.out.println("Login exitoso: " + response);
            System.out.println("ID del usuario: " + response.id());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en login: " + e.getMessage());
        }

        // Prueba 4: Login con contraseña incorrecta
        System.out.println("\n=== Prueba 4: Login con contraseña incorrecta ===");
        LoginRequest loginFailRequest = new LoginRequest("test2@example.com", "wrongpass");
        try {
            accesoService.login(loginFailRequest);
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // Prueba 5: Login con correo no existente
        System.out.println("\n=== Prueba 5: Login con correo no existente ===");
        LoginRequest loginNoExistRequest = new LoginRequest("noexist@example.com", "pass456");
        try {
            accesoService.login(loginNoExistRequest);
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

        // Prueba 6: Actualizar username
        System.out.println("\n=== Prueba 6: Actualizar username ===");
        ActualizarUsernameRequest updateRequest = new ActualizarUsernameRequest(1, "newuser1");
        try {
            accesoService.actualizarUsername(updateRequest);
            System.out.println("Username actualizado correctamente");
            // Verificar cambio con login
            UsuarioResponse response = accesoService.login(new LoginRequest("test2@example.com", "pass456"));
            System.out.println("Nuevo username: " + response.username());
        } catch (IllegalArgumentException e) {
            System.out.println("Error en actualización: " + e.getMessage());
        }

        // Prueba 7: Actualizar username con ID no existente
        System.out.println("\n=== Prueba 7: Actualizar username con ID no existente ===");
        ActualizarUsernameRequest updateFailRequest = new ActualizarUsernameRequest(999, "invaliduser");
        try {
            accesoService.actualizarUsername(updateFailRequest);
            System.out.println("Error: Debería haber fallado");
        } catch (IllegalArgumentException e) {
            System.out.println("Error esperado: " + e.getMessage());
        }

    }
}