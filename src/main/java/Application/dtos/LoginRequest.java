package Application.dtos;

/**
 * Record que representa la solicitud de inicio de sesión de un nuevo usuario.
 * Se usa un record para definir un contenedor de datos que transporta los datos del formulario de registro
 * desde el frontend al servicio de aplicación UsuarioService. Los records reducen el (constructor, getters,
 * equals, hashCode, toString) y garantizan que los datos no se modifiquen accidentalmente, lo que es ideal para DTOs en una arquitectura
 * DDD. Esto facilita al frontend enviar datos validados al servicio y asegura claridad en la transferencia de datos.
 */
public record LoginRequest(
        String correo,     // Correo del usuario para autenticación
        String contrasena  // Contraseña ingresada para verificar
) {}