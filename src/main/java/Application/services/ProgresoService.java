package Application.services;

import Application.dtos.progreso.ProgresoCursoResponse;
import Application.dtos.progreso.ProgresoResponse;
import Application.dtos.progreso.ProgresoRequest;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;

public class ProgresoService {

    private final InterfazProgresoRepository progresoRepository;

    public ProgresoService(InterfazProgresoRepository progresoRepository) {
        this.progresoRepository = progresoRepository;
    }

    public ProgresoResponse marcarCompletada(ProgresoRequest request) {
        progresoRepository.marcarCompletada(request.usuarioId(), request.leccionId());
        var progresoOpt = progresoRepository.findByUsuarioIdAndLeccionId(request.usuarioId(), request.leccionId());
        if (progresoOpt.isPresent()) {
            var p = progresoOpt.get();
            return new ProgresoResponse(p.getId(), p.getUsuarioId(), p.getLeccionId(), p.getEstado().name());
        }
        throw new RuntimeException("Error al obtener progreso después de completar");
    }

    public ProgresoCursoResponse obtenerProgresoPorCurso(int usuarioId, int cursoId) {
        double porcentaje = progresoRepository.obtenerProgresoPorCurso(usuarioId, cursoId);
        return new ProgresoCursoResponse(cursoId, porcentaje);
    }
}