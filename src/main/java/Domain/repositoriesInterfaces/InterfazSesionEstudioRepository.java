package Domain.repositoriesInterfaces;
import Domain.models.SesionEstudio;

import java.security.Timestamp;
import java.util.List;

public interface InterfazSesionEstudioRepository {
    List<SesionEstudio> buscarPorUsuarioYRangoFecha(
            int usuarioId,
            java.sql.Timestamp inicio,
            java.sql.Timestamp fin
    );

    SesionEstudio guardar(SesionEstudio sesion);
    SesionEstudio encontrarPorId(int id);

}
