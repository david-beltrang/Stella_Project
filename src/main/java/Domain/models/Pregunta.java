package Domain.models;

import java.util.List;
import java.util.Objects;

/**
 * Representa una pregunta evaluable.
 * Es necesario para el mapeo en LeccionRepository y el DTO en LeccionService.
 */
public class Pregunta {
    private final int id;
    private final String enunciado;
    private final List<Opcion> opciones;

    // NOTA: Se pueden añadir leccionId y pruebaId si se considera necesario en el Dominio.

    public Pregunta(int id, String enunciado, List<Opcion> opciones) {
        this.id = id;
        this.enunciado = Objects.requireNonNull(enunciado);
        this.opciones = Objects.requireNonNull(opciones);
    }

    // --- Getters de Dominio ---
    public int getId() { return id; }
    public String getEnunciado() { return enunciado; }
    public List<Opcion> getOpciones() { return opciones; }

    public Opcion getOpcionCorrecta() {
        return opciones.stream()
                .filter(Opcion::esCorrecta)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("La pregunta " + id + " no tiene una opción correcta definida."));
    }
}