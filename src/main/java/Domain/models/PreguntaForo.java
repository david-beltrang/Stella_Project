package Domain.models;

import java.time.LocalDateTime;

public class PreguntaForo {
    private final Integer id;
    private final Integer usuarioId;
    private final String titulo;
    private final String contenido;
    private final LocalDateTime fechaCreacion;

    private PreguntaForo(Integer id, Integer usuarioId, String titulo, String contenido, LocalDateTime fechaCreacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public static PreguntaForo crear(Integer usuarioId, String titulo, String contenido) {
        return new PreguntaForo(null, usuarioId, titulo, contenido, LocalDateTime.now());
    }

    public static PreguntaForo reconstruir(Integer id, Integer usuarioId, String titulo, String contenido,
            LocalDateTime fechaCreacion) {
        return new PreguntaForo(id, usuarioId, titulo, contenido, fechaCreacion);
    }

    public Integer getId() {
        return id;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
