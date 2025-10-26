package Application.dtos.leccion;

import Domain.models.LeccionValueObjects.EstadoProgreso;
import Domain.models.LeccionValueObjects.TipoContenido;

public record LeccionResponse(
        Integer id,
        String titulo,
        int numeroSeccion,
        TipoContenido tipoContenido,
        EstadoProgreso estado, // PENDIENTE, COMPLETADA
        boolean desbloqueada // Indica si se puede acceder o no
) {
}
