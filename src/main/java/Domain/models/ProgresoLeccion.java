package Domain.models;

import Domain.models.LeccionValueObjects.Estado;
import java.util.Objects;

public class ProgresoLeccion {
    private final Integer id;
    private final Integer usuarioId;
    private final Integer leccionId;
    private Estado estado;

    public ProgresoLeccion(Integer id, Integer usuarioId, Integer leccionId, Estado estado) {
        this.id = id;
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId no puede ser nulo");
        this.leccionId = Objects.requireNonNull(leccionId, "leccionId no puede ser nulo");
        this.estado = Objects.requireNonNull(estado, "estado no puede ser nulo");
    }

    public static ProgresoLeccion crear(Integer usuarioId, Integer leccionId, Estado estado) {
        return new ProgresoLeccion(null, usuarioId, leccionId, estado);
    }

    public static ProgresoLeccion reconstruir(Integer id, Integer usuarioId, Integer leccionId, Estado estado) {
        return new ProgresoLeccion(id, usuarioId, leccionId, estado);
    }

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public Integer getLeccionId() { return leccionId; }
    public Estado getEstado() { return estado; }
}