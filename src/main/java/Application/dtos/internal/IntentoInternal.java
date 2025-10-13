package Application.dtos.internal;

import java.time.LocalDateTime;

/**
 * dto de Mapeo utilizado por InterfazIntentoRepository para guardar un nuevo registro en la tabla 'intento'.
 * Contiene las referencias a las claves foráneas necesarias.
 */
public record IntentoInternal(
        // ID es null al crear un nuevo intento, la BD lo generará.
        Integer id,
        int usuarioId,
        int pruebaId,
        double score,
        // La fecha es opcional, ya que la BD puede aplicar el default `CURRENT_TIMESTAMP`.
        // Se incluye si el servicio quiere forzar un valor específico.
        LocalDateTime fecha
) {}
