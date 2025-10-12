package Application.dtos.sesionEstudio;

/**
 * DTO de respuesta para una opción de pregunta.
 * NO incluye 'esCorrecta' por seguridad.
 */
public record OpcionResponse(
        int id,
        String texto
) {}