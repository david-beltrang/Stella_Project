package Domain.repositoriesInterfaces;

import Domain.models.Curso;
import Domain.models.CursoValueObjects.CursoId;

public interface InterfazCursoRepository {
    /**
     * Busca un curso por su identificador único (VO).
     * @param cursoId El Value Object que encapsula el ID del curso.
     * @return La entidad Curso.
     */
    Curso buscarPorId(CursoId cursoId);
}
