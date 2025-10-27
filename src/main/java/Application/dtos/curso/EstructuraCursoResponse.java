package Application.dtos.curso;

import Application.dtos.seccion.SeccionResponse;
import Domain.models.CursoValueObjects.Titulo;

import java.util.List;

public record EstructuraCursoResponse(
        Integer id,
        Titulo tituloCurso,
        List<SeccionResponse> secciones
) {
}
