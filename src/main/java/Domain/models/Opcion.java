package Domain.models;

import java.util.Objects;

/**
 * Representa una opción de respuesta para una Pregunta.
 * Usado dentro de Pregunta.
 */
public class Opcion {
    private final int id;
    private final String texto;
    private final boolean esCorrecta;

    public Opcion(int id, String texto, boolean esCorrecta) {
        this.id = id;
        this.texto = Objects.requireNonNull(texto);
        this.esCorrecta = esCorrecta;
    }

    // --- Getters de Dominio ---
    public int getId() { return id; }
    public String getTexto() { return texto; }
    public boolean esCorrecta() { return esCorrecta; }
}