package Application.dtos.internal;

import java.util.List;

/**
 * dto usado internamente por la capa de Repositorio para manejar la estructura de preguntas y respuestas correctas.
 */
public record PreguntaConOpcionesInternal(
        int preguntaId,
        String enunciado,
        List<OpcionDetalleInternal> opciones // Usamos DTO de detalle para incluir si es correcta
) {}
