package Domain.repositoriesInterfaces;

import Domain.models.RespuestaForo;
import java.util.List;
import java.util.Optional;

public interface InterfazRespuestaForoRepository {
    void guardar(RespuestaForo respuesta);

    Optional<RespuestaForo> obtenerPorId(int id);

    List<RespuestaForo> listarPorPregunta(int preguntaId);

    void eliminar(int id);
}
