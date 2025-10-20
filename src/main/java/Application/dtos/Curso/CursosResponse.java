package Application.dtos.Curso;

import java.util.List;

public record CursosResponse(
        List<CursoResponse> cursosUsuario,
        List<CursoResponse> cursosDisponibles)
{}