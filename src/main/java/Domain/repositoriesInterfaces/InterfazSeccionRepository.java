package Domain.repositoriesInterfaces;

import Application.dtos.seccion.SeccionResponse;

import java.util.List;

public interface InterfazSeccionRepository {
    public List<SeccionResponse> encontrarSeccionesConLecciones(int cursoId);
}
