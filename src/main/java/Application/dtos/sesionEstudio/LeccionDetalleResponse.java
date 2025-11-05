package Application.dtos.sesionEstudio;

import Domain.models.LeccionValueObjects.TipoContenido;

import java.util.List;

// dto para devolver la información de una lección
public record LeccionDetalleResponse(
        int id,
        String titulo,
        int orden,
        TipoContenido tipoContenido,
        String contenidoHtml,
        String estadoProgreso,
        List<PreguntaResponse> preguntas
) {}