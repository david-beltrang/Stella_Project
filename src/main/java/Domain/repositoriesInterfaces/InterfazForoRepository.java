package Domain.repositoriesInterfaces;

import Application.dtos.foro.ComentarioResponse;
import Application.dtos.foro.PostResponse;
import Domain.models.Comentario;
import Domain.models.Post;

import java.util.List;

public interface InterfazForoRepository {

    List<PostResponse> obtenerTodos();

    PostResponse obtenerPostResponsePorId(int id);
    
    Comentario obtenerComentarioPorId(int id);

    Post obtenerPostPorId(int id);

    Post guardarPost(Post post);

    Comentario guardarComentario(Comentario comentario);

    void actualizarLikesPost(int postId, int likes);

    void actualizarLikesComentario(int comentario_id, int likes);

    ComentarioResponse obtenerComentarioResponsePorId(int comentarioId);
}
