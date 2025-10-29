package Application.services;

import Application.dtos.seccion.SeccionResponse;
import Domain.repositoriesInterfaces.InterfazSeccionRepository;
import java.util.List;

public class SeccionesService {
    private final InterfazSeccionRepository seccionRepository;

    public SeccionesService(InterfazSeccionRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
    }

    public List<SeccionResponse> ListarSeccionesConLecciones(int cursoId) {
        return seccionRepository.encontrarSeccionesConLecciones(cursoId); // Devuelve directamente el DTO del repositorio
    }
}
