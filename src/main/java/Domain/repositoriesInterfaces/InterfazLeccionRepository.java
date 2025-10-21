package Domain.repositoriesInterfaces;

import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.Leccion;

// No se define la Entidad Leccion completa, solo los métodos de lectura necesarios.
public interface InterfazLeccionRepository {

    /**
     * Busca una lección específica por su identificador.
     * @param leccionId El VO del ID de la lección.
     * @return La entidad Leccion.
     */
    Leccion buscarPorId(LeccionId leccionId);

    /**
     * Obtiene todas las lecciones de un curso, ordenadas por sección y orden.
     * @param cursoId El VO del ID del curso.
     * @return Iterable de Lecciones (para evitar dependencias de List).
     */
    Iterable<Leccion> buscarPorCursoId(Integer cursoId);

    /**
     * Busca la lección que precede inmediatamente a la lección actual,
     * basada en su orden y sección.
     * @return La entidad Leccion anterior, o null si es la primera.
     */
    Leccion buscarLeccionAnterior(Integer cursoId, int seccionActual, int ordenActual);

    /**
     * Busca la lección que sigue inmediatamente a la lección actual.
     * @return La entidad Leccion siguiente, o null si es la última.
     */
    Leccion buscarProximaLeccion(Integer cursoId, int seccionActual, int ordenActual);
}