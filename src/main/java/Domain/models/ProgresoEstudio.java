package Domain.models;

import java.time.LocalDate;
import java.util.Objects;

public class ProgresoEstudio {
    private final Integer id;
    private final Integer usuarioId;
    private final LocalDate fecha;
    private final boolean estudio;

    public ProgresoEstudio(Integer id, Integer usuarioId, LocalDate fecha, boolean estudio) {
        this.id = id;
        this.usuarioId = Objects.requireNonNull(usuarioId);
        this.fecha = Objects.requireNonNull(fecha);
        this.estudio = estudio;
    }

    public static ProgresoEstudio crear(Integer usuarioId, LocalDate fecha, boolean estudio) {
        return new ProgresoEstudio(null, usuarioId, fecha, estudio);
    }

    public Integer getId() {
        return id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public LocalDate getFecha() {
        return fecha;
    }
    
    public boolean isEstudio() {
        return estudio;
    }
}
