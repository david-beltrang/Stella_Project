// Domain/models/Comentario.java
package Domain.models;

import Domain.models.ForoValueObjects.Contenido;
import java.time.LocalDateTime;

public class Comentario {
    private final Integer id;
    private final Integer post_id;
    private final Integer usuario_id;
    private final Contenido contenido;
    private int likes;
    private LocalDateTime fechaCreacion;

    private Comentario(Integer id, Integer post_id, Integer usuario_id, Contenido contenido) {
        this.id = id;
        this.post_id = post_id;
        this.usuario_id = usuario_id;
        this.contenido = contenido;
        this.likes = 0;
        this.fechaCreacion = LocalDateTime.now();
    }

    public static Comentario crear(Integer post_id, Integer usuario_id, String contenidoTexto) {
        return new Comentario(null, post_id, usuario_id, new Contenido(contenidoTexto));
    }

    public static Comentario reconstruir(Integer id, Integer post_id, Integer usuario_id, Contenido contenido, int likes, LocalDateTime fechaCreacion) {
        Comentario comentario = new Comentario(id, post_id, usuario_id, contenido);
        comentario.likes = likes;
        comentario.fechaCreacion = fechaCreacion;
        return comentario;
    }

    public void incrementarLike() {
        likes++;
    }

    public Integer getId() { return id; }
    public Integer getPostId() { return post_id; }
    public Integer getUsuarioId() { return usuario_id; }
    public Contenido getContenido() { return contenido; }
    public int getLikes() { return likes; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}