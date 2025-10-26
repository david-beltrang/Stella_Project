package Application.dtos.seccion;

import Application.dtos.leccion.LeccionResponse;

import java.util.List;

public record Seccion(
        int numeroSeccion,
        String tituloSeccion,
        List<LeccionResponse> lecciones
) {
}
