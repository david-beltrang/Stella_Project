package Application.dtos.curso;

import Application.dtos.seccion.Seccion;
import Domain.models.CursoValueObjects.CursoId;

import java.util.List;

public record CursoEstructura(
        CursoId id,
        String tituloCurso,
        List<Seccion> secciones
) {
}
