package Domain.repositoriesInterfaces;

import Domain.models.Leccion;
import java.util.Optional;

public interface InterfazLeccionRepository {
    Optional<Leccion> findByCursoIdAndOrdenes(int cursoId, int numeroOrdenSeccion, int numeroOrdenLeccion);
}