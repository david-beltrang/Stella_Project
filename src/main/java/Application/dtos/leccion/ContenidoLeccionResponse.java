package Application.dtos.leccion;

import Domain.models.LeccionValueObjects.TipoContenido;

public record ContenidoLeccionResponse(
        Integer leccionId,
        String titulo,
        String contenido, // HTML o URL
        TipoContenido tipoContenido,
        boolean puedeAvanzar,
        boolean puedeRetroceder
) {
}
