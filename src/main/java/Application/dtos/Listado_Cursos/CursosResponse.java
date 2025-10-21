package Application.dtos.Listado_Cursos;

import java.util.List;

public record CursosResponse(
        List<CursoResponse> cursosUsuario,
        List<CursoResponse> cursosDisponibles)
{}