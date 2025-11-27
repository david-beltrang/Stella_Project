package Domain.repositoriesInterfaces;

import Domain.models.ProgresoGamificacion;
import java.util.Optional;

public interface InterfazProgresoGamificacionRepository {

    Optional<ProgresoGamificacion> obtenerPorUsuarioId(int usuarioId);

    void guardar(ProgresoGamificacion progreso);

    void agregarPuntos(int usuarioId, int puntos);

    void incrementarNivel(int usuarioId);

    void actualizarRacha(int usuarioId, int nuevaRacha);
}
