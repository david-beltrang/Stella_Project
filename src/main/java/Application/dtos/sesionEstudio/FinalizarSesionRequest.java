package Application.dtos.sesionEstudio;

// dto de solicitud para finalizar la sesión de estudio y actualizar el progreso
public record FinalizarSesionRequest(
        int usuarioId,
        int leccionId,
        int tiempoEstudioSegundos,
        int tiempoDescansoSegundos
) {}
