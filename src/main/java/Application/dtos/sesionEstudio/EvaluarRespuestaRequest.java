package Application.dtos.sesionEstudio;

/**
 * dto de solicitud para enviar la respuesta de una pregunta.
 */
public record EvaluarRespuestaRequest(
        int usuarioId,
        int leccionId, // Contexto de la lección
        int preguntaId,
        int opcionSeleccionadaId
) {}