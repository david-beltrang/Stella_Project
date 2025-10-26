package Application.dtos.sesionEstudio;

public record SesionCompletadaResponse(
        boolean exito,
        String mensaje,
        int pescaditosObtenidos // Campo de gamificación
) {
}
