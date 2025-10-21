package Application.dtos.curso;

import Application.dtos.seccion.Seccion;

import java.util.List;

public record CursoEstructura(
        Integer id,
        String tituloCurso,
        List<Seccion> secciones
) {
}
