package Application.dtos.curso;

import Application.dtos.seccion.Seccion;
import Domain.models.CursoValueObjects.Titulo;

import java.util.List;

public record EstructuraCursoResponse(
        Integer id,
        Titulo tituloCurso,
        List<Seccion> secciones
) {
}
