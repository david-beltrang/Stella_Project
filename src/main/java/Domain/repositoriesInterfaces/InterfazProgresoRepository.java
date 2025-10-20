package Domain.repositoriesInterfaces;

import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.ProgresoLeccion;
import Domain.models.UsuarioValueObjects.UsuarioId;

public interface InterfazProgresoRepository {
    /**
     * Obtiene el progreso de una lección específica para un usuario dado.
     * @return La entidad ProgresoLeccion.
     */
    ProgresoLeccion buscarPorUsuarioYLeccion(UsuarioId usuarioId, LeccionId leccionId);

    /**
     * Persiste (guarda o actualiza) el estado de progreso de una lección.
     * @param progreso La entidad ProgresoLeccion a guardar.
     * @return La entidad guardada, posiblemente con un ID asignado por la DB.
     */
    ProgresoLeccion guardar(ProgresoLeccion progreso);
}
