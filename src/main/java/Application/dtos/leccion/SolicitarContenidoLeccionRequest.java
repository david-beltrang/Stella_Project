package Application.dtos.leccion;

import Domain.models.CursoValueObjects.LeccionId;

public record SolicitarContenidoLeccionRequest(
        LeccionId leccionId
) {
}
