package Application.services;

import Application.dtos.leccion.LeccionResponse;
import Domain.models.Leccion;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import java.util.Optional;

public class LeccionService {

    private final InterfazLeccionRepository leccionRepository;

    public LeccionService(InterfazLeccionRepository leccionRepository) {
        this.leccionRepository = leccionRepository;
    }

    public LeccionResponse obtenerLeccionPorCursoYOrden(
            int cursoId, int numeroOrdenSeccion, int numeroOrdenLeccion) {

        Optional<Leccion> leccionOpt = leccionRepository.findByCursoIdAndOrdenes(
                cursoId, numeroOrdenSeccion, numeroOrdenLeccion);

        if (leccionOpt.isEmpty()) {
            throw new RuntimeException(
                    "Lección no encontrada: cursoId=" + cursoId +
                            ", sección orden=" + numeroOrdenSeccion +
                            ", lección orden=" + numeroOrdenLeccion);
        }

        Leccion leccion = leccionOpt.get();
        return new LeccionResponse(
                leccion.getId(),
                leccion.getSeccion_id(),
                leccion.getTitulo().valorTitulo(),
                leccion.getNumeroOrden(),
                leccion.getTipoContenido().name(),
                leccion.getUrl_video(),
                leccion.getContenido()
        );
    }
}