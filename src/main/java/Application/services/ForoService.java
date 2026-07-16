// Application/services/ForoService.java
package Application.services;

import Application.dtos.foro.*;
import Domain.models.Post;
import Domain.models.Comentario;
import Domain.repositoriesInterfaces.InterfazForoRepository;

import java.util.List;

/**
 * Servicio para el foro estilo post/comentario.
 * @deprecated Reemplazado por {@link PreguntasRespuestasForoService} (foro Q&A).
 * Se mantiene solo para no romper ForoService_IntegrationTest. No usar en nuevo código.
 */
@Deprecated
public class ForoService {
    private final InterfazForoRepository foroRepository;

    public ForoService(InterfazForoRepository foroRepository) {
        this.foroRepository = foroRepository;
    }

    /**
     * Obtiene todos los posts junto con sus comentarios.
     * @return Lista de PostResponse con sus comentarios asociados.
     * @deprecated Usar PreguntasRespuestasForoService en su lugar.
     */
    @Deprecated
    public List<PostResponse> obtenerTodosLosPosts() {
        return foroRepository.obtenerTodos();
    }

    /**
     * Obtiene un post específico por su ID junto con sus comentarios.
     * @param id ID del post a buscar.
     * @return PostResponse con los detalles del post y sus comentarios, o null si no existe.
     * @deprecated Usar PreguntasRespuestasForoService en su lugar.
     */
    @Deprecated
    public PostResponse obtenerPostPorId(int id) {
        PostResponse post = foroRepository.obtenerPostResponsePorId(id);
        if (post == null) {
            throw new RuntimeException("Post no encontrado con id: " + id);
        }
        return post;
    }

    /**
     * Crea un nuevo post en el foro.
     * @param request DTO con los datos del nuevo post.
     * @return PostResponse con los detalles del post creado.
     * @throws IllegalArgumentException si el contenido es inválido.
     */
    public PostResponse crearPost(CrearPostRequest request) {
        if (request == null || request.contenido() == null || request.contenido().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del post es obligatorio.");
        }
        Post post = Post.crear(request.usuarioId(), request.contenido(), request.etiqueta());
        Post postGuardado = foroRepository.guardarPost(post);
        return new PostResponse(
                postGuardado.getId(),
                postGuardado.getUsuarioId(),
                postGuardado.getContenido().texto(),
                postGuardado.getLikes(),
                postGuardado.getFechaCreacion(),
                postGuardado.getEtiqueta(),
                List.of() // Comentarios se cargan en el repositorio
        );
    }

    /**
     * Crea un nuevo comentario para un post existente.
     * @param request DTO con los datos del nuevo comentario.
     * @return ComentarioResponse con los detalles del comentario creado.
     * @throws IllegalArgumentException si el contenido es inválido o el post no existe.
     */
    public ComentarioResponse crearComentario(CrearComentarioRequest request) {
        if (request == null || request.contenidoTexto() == null || request.contenidoTexto().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del comentario es obligatorio.");
        }
        if (foroRepository.obtenerPostPorId(request.postId()) == null) {
            throw new IllegalArgumentException("El post especificado no existe.");
        }
        Comentario comentario = Comentario.crear(request.postId(), request.usuarioId(), request.contenidoTexto());
        Comentario comentarioGuardado = foroRepository.guardarComentario(comentario);
        return new ComentarioResponse(
                comentarioGuardado.getId(),
                comentarioGuardado.getUsuarioId(),
                comentarioGuardado.getContenido().texto(),
                comentarioGuardado.getFechaCreacion(),
                comentarioGuardado.getLikes()
        );
    }

    /**
     * Incrementa los likes de un post.
     * @param postId ID del post al que se le dará like.
     * @return PostResponse actualizado con el nuevo número de likes.
     * @throws IllegalArgumentException si el post no existe.
     */
    public PostResponse darLikeAPost(int postId) {
        Post post = foroRepository.obtenerPostPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("El post no existe.");
        }
        int newLikes = post.getLikes() + 1;
        foroRepository.actualizarLikesPost(postId, newLikes);
        return foroRepository.obtenerPostResponsePorId(postId);
    }

    /**
     * Incrementa los likes de un comentario.
     * @param comentarioId ID del comentario al que se le dará like.
     * @return ComentarioResponse actualizado con el nuevo número de likes.
     * @throws IllegalArgumentException si el comentario no existe.
     */
    public ComentarioResponse darLikeAComentario(int comentarioId) {
        Comentario comentario = foroRepository.obtenerComentarioPorId(comentarioId);
        if (comentario == null) {
            throw new IllegalArgumentException("El comentario no existe.");
        }
        int newLikes = comentario.getLikes() + 1;
        foroRepository.actualizarLikesComentario(comentarioId, newLikes);
        return foroRepository.obtenerComentarioResponsePorId(comentarioId);
    }
}