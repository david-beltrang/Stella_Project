package Domain.repositoriesInterfaces;

import Application.dtos.internal.ContenidoLeccionInternal; // Asume la creación de un DTO para el contenido
import Domain.models.Leccion;
import Domain.models.Pregunta;

import java.util.List;
import java.util.Optional;

// No se define la Entidad Leccion completa, solo los métodos de lectura necesarios.
public interface InterfazLeccionRepository {

    /**
     * Busca todas las lecciones que pertenecen a un curso y número de sección específicos,
     * ordenadas por su número de orden.
     * @param cursoId ID del curso.
     * @param numeroSeccion Número de la sección dentro del curso.
     * @return Lista de Lecciones.
     */
    List<Leccion> buscarPorCursoYSeccion(int cursoId, int numeroSeccion);

    /**
     * Busca las preguntas asociadas a una lección específica.
     * Esto es usado por LeccionService para mostrar el contenido del quiz/práctica.
     * @param leccionId ID de la lección.
     * @return Lista de Preguntas con sus opciones.
     */
    List<Pregunta> buscarPreguntasAsociadas(int leccionId);

    /**
     * Obtiene el contenido específico de una lección, mapeado a un DTO interno.
     * @param leccionId ID de la lección.
     * @return DTO con el contenido (HTML, tipo, pruebaId).
     */
    Optional<ContenidoLeccionInternal> obtenerContenido(int leccionId);
}