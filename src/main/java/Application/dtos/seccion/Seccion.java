package Application.dtos.seccion;

import Application.dtos.leccion.LeccionLista;

import java.util.List;

public record Seccion(
        int numeroSeccion,
        String tituloSeccion,
        List<LeccionLista> lecciones
) {
}
