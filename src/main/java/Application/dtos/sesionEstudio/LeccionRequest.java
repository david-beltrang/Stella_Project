package Application.dtos.sesionEstudio;

/**
 * dto de solicitud para iniciar o continuar una sesión de estudio.
 */
public record LeccionRequest(
        int usuarioId,
        int leccionId
) {}