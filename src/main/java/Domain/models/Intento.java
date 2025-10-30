package Domain.models;

import Domain.models.PruebaValueObjects.Puntaje;

import java.time.LocalDateTime;
import java.util.Objects;

public class Intento {
    private final Integer id;
    private final Integer usuario_id;
    private final Integer prueba_id;
    private final Puntaje puntaje;
    private final LocalDateTime fechaIntento;

    private Intento(Integer id, Integer usuario_id, Integer prueba_id, Puntaje puntaje, LocalDateTime fechaIntento) {
        this.id = id;
        this.usuario_id = Objects.requireNonNull(usuario_id, "el id de usuario no puede ser nulo");
        this.prueba_id = Objects.requireNonNull(prueba_id, "el id de prueba no puede ser nulo");;
        this.puntaje = Objects.requireNonNull(puntaje, "el puntaje no puede ser nulo");;
        this.fechaIntento = (fechaIntento != null) ? fechaIntento : LocalDateTime.now();
    }

    public static Intento crear(Integer usuarioId, Integer pruebaId, Puntaje puntaje) {
        return new Intento(null, usuarioId, pruebaId, puntaje, LocalDateTime.now());
    }

    public static Intento reconstruir(Integer id, Integer usuarioId, Integer pruebaId, Puntaje puntaje, LocalDateTime fechaIntento) {
        return new Intento(id, usuarioId, pruebaId, puntaje, fechaIntento);
    }

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuario_id; }
    public Integer getPruebaId() { return prueba_id; }
    public Puntaje getPuntaje() { return puntaje; }
    public LocalDateTime getFechaIntento() { return fechaIntento; }
}
