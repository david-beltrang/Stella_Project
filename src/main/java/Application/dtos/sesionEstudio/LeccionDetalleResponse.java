package Application.dtos.sesionEstudio;

import java.util.List;

// dto para devolver la información de una lección
public record LeccionDetalleResponse(
        int id,
        String titulo,
        int orden,
        String tipoContenido,
        String contenidoHtml,
        String estadoProgreso,
        List<PreguntaResponse> preguntas
) {}