package Domain.models;

import java.util.Objects;


public class Opcion {
    private final Integer id;
    private final Integer pregunta_id;
    private final String texto;
    private final boolean esCorrecta;

    private Opcion(Integer id, Integer pregunta_id, String texto, boolean esCorrecta) {
        this.id = id;
        this.pregunta_id = Objects.requireNonNull(pregunta_id, "El id de pregunta no puede ser nulo");
        this.texto = Objects.requireNonNull(texto, "El id de pregunta no puede ser nulo");
        this.esCorrecta = Objects.requireNonNull(esCorrecta, "El atributo 'es correcta' no puede ser nulo");
    }

    public static Opcion crear(Integer pregunta_id, String texto, boolean esCorrecta) {
        return new Opcion(null, pregunta_id, texto, esCorrecta);
    }

    public static Opcion reconstruir(Integer id, Integer pregunta_id, String texto, boolean esCorrecta) {
        return new Opcion(id, pregunta_id, texto, esCorrecta);
    }

    public Integer getId() { return id; }
    public Integer getPreguntaId() { return pregunta_id; }
    public String getTexto() { return texto; }
    public boolean isEsCorrecta() { return esCorrecta; }
}