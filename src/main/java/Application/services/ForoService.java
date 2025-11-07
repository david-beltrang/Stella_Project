// Application/services/ForoService.java
package Application.services;

import Application.dtos.Foro.*;
import Domain.models.Post;
import Domain.models.Comentario;
import Domain.repositoriesInterfaces.InterfazForoRepository;

import java.util.List;

public class ForoService {
    private final InterfazForoRepository foroRepository;

    public ForoService(InterfazForoRepository foroRepository) {
        this.foroRepository = foroRepository;
    }

    public List<PostResponse> obtenerTodosLosPosts() {
        return foroRepository.obtenerTodos();
    }

    public PostResponse crearPost(CrearPostRequest request) {
        Post post = Post.crear(request.usuarioId(), request.contenido());
        Post guardado = foroRepository.guardarPost(post);
        return new PostResponse(
                guardado.getId(),
                guardado.getUsuarioId(),
                guardado.getContenido().texto(),
                guardado.getLikes(),
                guardado.getFechaCreacion(),
                List.of() // Comentarios se cargan en el repositorio
        );
    }

    public ComentarioResponse crearComentario(CrearComentarioRequest request) {
        Comentario comentario = Comentario.crear(
                request.postId(),
                request.usuarioId(),
                request.contenidoTexto()
        );
        Comentario guardado = foroRepository.guardarComentario(comentario);
        return new ComentarioResponse(
                guardado.getId(),
                guardado.getUsuarioId(),
                guardado.getContenido().texto(),
                guardado.getFechaCreacion(),
                guardado.getLikes()
        );
    }

    public PostResponse darLikeAPost(int postId) {
        Post post = foroRepository.obtenerPostPorId(postId);
        post.incrementarLike();
        foroRepository.actualizarLikesPost(postId, post.getLikes());
        return foroRepository.obtenerPostResponsePorId(postId);
    }

    public ComentarioResponse darLikeAComentario(int comentario_id) {
        Comentario comentario = foroRepository.obtenerComentarioPorId(comentario_id);
        comentario.incrementarLike();
        foroRepository.actualizarLikesComentario(comentario_id, comentario.getLikes());
        return foroRepository.obtenerComentarioResponsePorId(comentario_id);
    }
}