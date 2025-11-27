package Application.dtos.sesionEstudio.Pomodoro;

import java.sql.Timestamp;

public record SesionEstudioResponse(
        Integer id,
        int usuarioId,
        int tiempoEstudio,
        int tiempoDescanso,
        Timestamp fechaInicio,
        Timestamp fechaFin
) {}