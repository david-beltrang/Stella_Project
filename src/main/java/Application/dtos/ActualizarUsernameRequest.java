package Application.dtos;

/**
 * Record que representa la solicitud para actualizar el username de un usuario.
 * Se usa un record para definir un contenedor inmutable y ligero que transporta el ID del usuario y el nuevo username desde el frontend
 * (JavaFX) al servicio de aplicación. Los records son ideales para DTOs porque reducen el código repetitivo (constructor, getters, equals, etc.)
 * y previenen modificaciones accidentales de los datos, lo que es clave en una arquitectura DDD para mantener la integridad de las solicitudes.
 * Esto hace que el frontend pueda enviar datos de forma clara y que el servicio procese la actualización sin ambigüedades.
 */
public record ActualizarUsernameRequest(
        int id,           // ID del usuario, obtenido de la sesión activa
        String nuevoUsername // Nuevo username propuesto, validado en el dominio segun el VO value object
) {}