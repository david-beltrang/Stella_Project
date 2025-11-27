package Domain.repositoriesInterfaces;

import Domain.models.Ejercicio;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio para ejercicios de programación
 */
public interface InterfazEjercicioRepository {

    /**
     * Guarda un nuevo ejercicio en la base de datos
     */
    Ejercicio guardar(Ejercicio ejercicio);

    /**
     * Obtiene un ejercicio por su ID
     */
    Optional<Ejercicio> obtenerPorId(int id);

    /**
     * Lista todos los ejercicios de una lección
     */
    List<Ejercicio> listarPorLeccion(int leccionId);

    /**
     * Elimina un ejercicio
     */
    void eliminar(int id);
}
