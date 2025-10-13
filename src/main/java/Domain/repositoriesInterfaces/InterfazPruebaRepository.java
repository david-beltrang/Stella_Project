package Domain.repositoriesInterfaces;

import Domain.models.Prueba;
import Application.dtos.internal.PreguntaConOpcionesInternal; // Asume la creación de un DTO para preguntas

import java.util.List;
import java.util.Optional;

public interface InterfazPruebaRepository {

    // Busca una prueba asociada a una lección (si existe).
    Optional<Prueba> buscarPruebaPorLeccion(int leccionId);

    // Obtiene todas las preguntas y opciones asociadas a una prueba.
    List<PreguntaConOpcionesInternal> obtenerPreguntas(int pruebaId);

    // Obtiene una lista de DTOs de preguntas (generalmente solo uno) y sus opciones detalladas para fines de evaluación.
    List<PreguntaConOpcionesInternal> obtenerPreguntasPorId(int preguntaId);
}