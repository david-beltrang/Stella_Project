package Domain.repositoriesInterfaces;

import Domain.models.ProgresoEstudio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface InterfazProgresoEstudioRepository {
    Optional<ProgresoEstudio> obtenerPorUsuarioYFecha(int usuarioId, LocalDate fecha);
    List<ProgresoEstudio> listarPorRango(int usuarioId, LocalDate inicio, LocalDate fin);
    void guardar(ProgresoEstudio progreso);
    void actualizar(ProgresoEstudio progreso);
}
