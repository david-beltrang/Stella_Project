package Application.dtos.internal;

/**
 * dto utilizado por InterfazIntentoRepository para guardar un nuevo registro en la tabla 'respuesta'.
 * Contiene las referencias a las claves foráneas necesarias.
 */
public record RespuestaInternal(
        // ID es null al crear una nueva respuesta.
        Integer id,
        int intentoId, // Clave foránea al Intento recién guardado
        int preguntaId,
        int opcionId // Opción seleccionada por el usuario
) {}