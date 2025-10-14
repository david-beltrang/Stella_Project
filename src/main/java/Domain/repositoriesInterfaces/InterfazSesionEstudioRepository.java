package Domain.repositoriesInterfaces;
import Domain.models.SesionEstudio;

public interface InterfazSesionEstudioRepository {
    SesionEstudio guardar(SesionEstudio sesion);
    SesionEstudio encontrarPorId(int id);
}
