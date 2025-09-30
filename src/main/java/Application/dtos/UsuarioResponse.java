package Application.dtos;

/**
 * Record que representa la respuesta enviada al frontend tras operaciones de registro o login.
 * Usamos un record para crear un contenedor de datos inmutable y conciso que devuelve información del usuario (sin la contraseña por seguridad)
 * al frontend (JavaFX). Los records simplifican el código al generar automáticamente constructor, getters, equals, hashCode, y toString,
 * lo que reduce errores y mejora la mantenibilidad. En DDD, este DTO asegura una interfaz clara entre la capa de aplicación y el frontend,
 * proporcionando el ID del usuario para operaciones futuras como iniciar sesiones de estudio.
 */
public record UsuarioResponse(
        int id,           // ID del usuario, clave para asociar con sesiones de estudio o estadísticas
        String username,  // Username único, mostrado en la UI
        String correo,    // Correo del usuario, usado para identificación
        String nombre,    // Nombre completo, mostrado en la UI
        String tipo       // Tipo de usuario (e.g., "ESTUDIANTE", "ADMIN"), usado para permisos o roles
) {}