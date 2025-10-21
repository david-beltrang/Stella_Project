package Application.dtos.Listado_Cursos;

public record CursoResponse(
        int id,
        String titulo,
        String descripcion,
        String nivel,
        String categoria,
        int duracionMinutos)
{}
