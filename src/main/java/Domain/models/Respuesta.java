package Domain.models;

import java.util.Objects;

public class Respuesta {
    private final Integer id;
    private final Integer intento_id;
    private final Integer pregunta_id;
    private final Integer opcion_seleccionada_id;

    private Respuesta(Integer id, Integer intento_id, Integer pregunta_id, Integer opcion_seleccionada_id) {
        this.id = id;
        this.intento_id = intento_id;
        this.pregunta_id = Objects.requireNonNull(pregunta_id, "el id de la pregunta no puede ser nulo");
        this.opcion_seleccionada_id = opcion_seleccionada_id;
    }

    public static Respuesta crear(Integer intento_id, Integer pregunta_id, Integer opcion_seleccionada_id) {
        return new Respuesta(null, intento_id, pregunta_id, opcion_seleccionada_id);
    }

    public Integer getId() { return id; }
    public Integer getIntentoId() { return intento_id; }
    public Integer getPreguntaId() { return pregunta_id; }
    public Integer getOpcionSeleccionadaId() { return opcion_seleccionada_id; }
}
