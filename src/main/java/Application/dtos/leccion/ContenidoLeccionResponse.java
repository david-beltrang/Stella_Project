package Application.dtos.leccion;

import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.CursoValueObjects.TipoContenido;

public record ContenidoLeccionResponse(
        LeccionId leccionId,
        String titulo,
        String contenido, // HTML o URL
        TipoContenido tipoContenido,
        boolean puedeAvanzar,
        boolean puedeRetroceder
) {
}
