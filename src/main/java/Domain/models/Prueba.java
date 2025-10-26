package Domain.models;

import java.util.Objects;

/**
 * Aggregate Root: Representa una prueba o quiz dentro de una sección.
 */
public class Prueba {

    private Integer id;
    private final int seccionId;
    private final String lenguaje;
    private final String tipo; // QUIZ o EXAMEN

    // ------------------ CONSTRUCTOR PRIVADO ----------------------

    private Prueba(Integer id, int seccionId, String lenguaje, String tipo) {
        this.id = id;
        this.seccionId = seccionId;
        this.lenguaje = Objects.requireNonNull(lenguaje, "El lenguaje no puede ser nulo.");
        this.tipo = Objects.requireNonNull(tipo, "El tipo de prueba no puede ser nulo.");
    }

    // ------------------ FACTORY METHODS ------------------

    public static Prueba crearNueva(int seccionId, String lenguaje, String tipo) {
        // En un proyecto completo, 'lenguaje' y 'tipo' tendrían VOs. Aquí, se usa String por simplicidad.
        if (lenguaje.isBlank() || tipo.isBlank()) {
            throw new IllegalArgumentException("Lenguaje o tipo de prueba no pueden ser vacíos.");
        }
        return new Prueba(null, seccionId, lenguaje.toUpperCase(), tipo.toUpperCase());
    }

    public static Prueba reconstruir(Integer id, int seccionId, String lenguaje, String tipo) {
        return new Prueba(id, seccionId, lenguaje, tipo);
    }

    // ---------- GETTERS ---------

    public Integer getId() { return id; }
    public int getSeccionId() { return seccionId; }
    public String getLenguaje() { return lenguaje; }
    public String getTipo() { return tipo; }
}