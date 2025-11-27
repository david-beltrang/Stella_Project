package Domain.models;

import java.time.LocalDateTime;

public class RespuestaForo {
    private final Integer id;
    private final Integer preguntaId;
    private final Integer usuarioId;
    private final String contenido;
    private final LocalDateTime fechaCreacion;

    private RespuestaForo(Integer id, Integer preguntaId, Integer usuarioId, String contenido,
            LocalDateTime fechaCreacion) {
        this.id = id;
        this.preguntaId = preguntaId;
        this.usuarioId = usuarioId;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public static RespuestaForo crear(Integer preguntaId, Integer usuarioId, String contenido) {
        return new RespuestaForo(null, preguntaId, usuarioId, contenido, LocalDateTime.now());
    }

    public static RespuestaForo reconstruir(Integer id, Integer preguntaId, Integer usuarioId, String contenido,
            LocalDateTime fechaCreacion) {
        return new RespuestaForo(id, preguntaId, usuarioId, contenido, fechaCreacion);
    }

    public Integer getId() {
        return id;
    }

    public Integer getPreguntaId() {
        return preguntaId;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
