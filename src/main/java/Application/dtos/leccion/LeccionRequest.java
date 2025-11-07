package Application.dtos.leccion;

// dto de solicitud para iniciar o continuar una sesión de estudio
public record LeccionRequest(
        int usuarioId,
        int leccionId
) {}