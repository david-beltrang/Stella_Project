package Domain.repositoriesInterfaces;

import Domain.models.ProgresoLeccion;
import Domain.models.LeccionValueObjects.EstadoLeccion;
import java.util.Optional;

public interface InterfazProgresoLeccionRepository {

    // Busca el progreso específico de un usuario en una lección.
    Optional<ProgresoLeccion> buscarPorUsuarioYLeccion(int usuarioId, int leccionId);

    /**
     * Guarda o actualiza el progreso. Debe manejar si existe o si es un nuevo registro.
     * @return El ProgresoLeccion guardado.
     */
    ProgresoLeccion guardar(ProgresoLeccion progreso);

    // Actualiza el estado de progreso de una lección ya existente.
    void actualizarEstado(int progresoId, EstadoLeccion nuevoEstado);
}

