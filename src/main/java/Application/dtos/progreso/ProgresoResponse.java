package Application.dtos.progreso;

public record ProgresoResponse(
        int id,
        int usuarioId,
        int leccionId,
        String estado
) {}