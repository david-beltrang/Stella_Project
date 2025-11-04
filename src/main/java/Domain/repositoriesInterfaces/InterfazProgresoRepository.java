package Domain.repositoriesInterfaces;

import Domain.models.ProgresoLeccion;
import java.util.Optional;

public interface InterfazProgresoRepository {
    void marcarCompletada(int usuarioId, int leccionId);
    double obtenerProgresoPorCurso(int usuarioId, int cursoId);
    Optional<ProgresoLeccion> findByUsuarioIdAndLeccionId(int usuarioId, int leccionId);  // Opcional, para verificaciones
}