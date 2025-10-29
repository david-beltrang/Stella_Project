package Application.dtos.seccion;

import Application.dtos.leccion.LeccionResponse;

import java.util.List;

public record SeccionResponse(
        int id,
        String titulo,
        int numeroOrden,
        List<LeccionResponse> lecciones
) {
}
