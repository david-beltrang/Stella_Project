// Domain/models/Post.java
package Domain.models;

import Domain.models.ForoValueObjects.Contenido;
import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private final Integer id;
    private final Integer usuario_id;
    private final Contenido contenido;
    private int likes;
    private LocalDateTime fechaCreacion;
    private String etiqueta;

    private Post(Integer id, Integer usuario_id, Contenido contenido, String etiqueta) {
        this.id = id;
        this.usuario_id = Objects.requireNonNull(usuario_id, "El id de usuario no puede ser nulo");
        this.contenido = contenido;
        this.likes = 0;
        this.fechaCreacion = LocalDateTime.now();
        this.etiqueta = etiqueta;
    }

    public static Post crear(Integer usuario_id, String contenidoTexto, String etiqueta) {
        return new Post(null, usuario_id, new Contenido(contenidoTexto), etiqueta);
    }

    public static Post reconstruir(Integer id, Integer usuarioId, Contenido contenido, int likes, LocalDateTime fechaCreacion, String etiqueta) {
        Post post = new Post(id, usuarioId, contenido, etiqueta);
        post.likes = likes;
        post.fechaCreacion = fechaCreacion;
        return post;
    }

    public void incrementarLike() {
        likes++;
    }

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuario_id; }
    public Contenido getContenido() { return contenido; }
    public int getLikes() { return likes; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public String getEtiqueta() { return etiqueta; }
}