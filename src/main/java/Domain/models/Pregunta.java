package Domain.models;

import java.util.Objects;


public class Pregunta {
    private final Integer id;
    private final String enunciado;
    private final Integer prueba_id;

    private Pregunta(Integer id, String enunciado, Integer prueba_id) {
        this.id = id;
        this.enunciado = Objects.requireNonNull(enunciado, "El enunciado no puede ser nulo");
        this.prueba_id = Objects.requireNonNull(prueba_id, "El id de prueba no puede ser nulo");;
    }

    public static Pregunta crear(String enunciado, Integer pruebaId) {
        return new Pregunta(null, enunciado, pruebaId);
    }

    public static Pregunta reconstruir(Integer id, String enunciado, Integer pruebaId) {
        return new Pregunta(id, enunciado, pruebaId);
    }

    public Integer getId() { return id; }
    public String getEnunciado() { return enunciado; }
    public Integer getPruebaId() { return prueba_id; }
}