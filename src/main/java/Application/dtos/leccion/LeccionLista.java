package Application.dtos.leccion;

import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.CursoValueObjects.EstadoProgreso;
import Domain.models.CursoValueObjects.TipoContenido;

public record LeccionLista(
        LeccionId id,
        String titulo,
        int numeroSeccion,
        TipoContenido tipoContenido,
        EstadoProgreso estado, // PENDIENTE, COMPLETADA
        boolean desbloqueada // Indica si se puede acceder o no
) {
}
