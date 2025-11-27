package Domain.repositoriesInterfaces;

import Domain.models.PreguntaForo;
import java.util.List;
import java.util.Optional;

public interface InterfazPreguntaForoRepository {
    void guardar(PreguntaForo pregunta);

    Optional<PreguntaForo> obtenerPorId(int id);

    List<PreguntaForo> listarTodas();

    void eliminar(int id);
}
