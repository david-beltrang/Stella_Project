package Application.dtos.sesionEstudio;

import java.util.List;

/**
 * DTO para la respuesta detallada de una lección, incluyendo el progreso.
 */
public record LeccionDetalleResponse(
        int id,
        String titulo,
        int orden,
        String tipoContenido,
        String contenidoHtml,
        String estadoProgreso,
        List<PreguntaResponse> preguntas
) {}