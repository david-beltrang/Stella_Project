package Domain.repositoriesInterfaces;
import Domain.models.SesionEstudio;

import java.sql.Timestamp;
import java.util.List;

public interface InterfazSesionEstudioRepository {
    SesionEstudio guardar(SesionEstudio sesion);
    SesionEstudio encontrarPorId(int id);

    List<SesionEstudio> buscarPorUsuarioYRangoFecha(int usuarioId, Timestamp startTs, Timestamp endTs);
}
