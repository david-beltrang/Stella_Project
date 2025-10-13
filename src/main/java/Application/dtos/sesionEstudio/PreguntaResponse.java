package Application.dtos.sesionEstudio;

import java.util.List;

/**
 * DTO de respuesta para una pregunta con sus opciones.
 */
public record PreguntaResponse(
        int id,
        String enunciado,
        List<OpcionResponse> opciones
) {}