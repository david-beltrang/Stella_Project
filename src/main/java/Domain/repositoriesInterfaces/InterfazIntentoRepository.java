package Domain.repositoriesInterfaces;
import Domain.models.Intento;
import Domain.models.Respuesta;
import java.util.List;

public interface InterfazIntentoRepository {
    Intento guardar(Intento intento);
    void guardarRespuestas(List<Respuesta> respuestas);
}
