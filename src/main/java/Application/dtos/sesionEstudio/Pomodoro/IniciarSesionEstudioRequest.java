package Application.dtos.sesionEstudio.Pomodoro;

public record IniciarSesionEstudioRequest(int usuarioId, int tiempoEstudio, int tiempoDescanso) {}