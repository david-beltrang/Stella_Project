package Infrastructure.test_temporal;

import Application.dtos.Foro.ComentarioResponse;
import Application.dtos.Foro.PostResponse;
import Application.services.ForoService;
import Application.dtos.Foro.CrearPostRequest;
import Application.dtos.Foro.CrearComentarioRequest;
import Domain.repositoriesInterfaces.InterfazForoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.ForoRepository;
import Infrastructure.persistence.IConexionBD;

import java.util.List;


public class ForoServiceTest {
    public static void main(String[] args) {
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();
        System.out.println("Inicialización completada, iniciando prueba...");

        // Inicializar repositorio y servicio
        InterfazForoRepository foroRepository = new ForoRepository(connMgr);
        ForoService foroService = new ForoService(foroRepository);

        // 1. Probar obtener todos los posts existentes
        System.out.println("=== Obtener todos los posts ===");
        List<PostResponse> todosLosPosts = foroService.obtenerTodosLosPosts();
        for (PostResponse post : todosLosPosts) {
            System.out.println("Post: ID=" + post.id() + ", Usuario=" + post.usuarioId() + ", Contenido=" + post.contenido() +
                    ", Likes=" + post.likes() + ", Etiqueta=" + post.etiqueta() + ", Fecha=" + post.fechaCreacion());
            for (ComentarioResponse comentario : post.comentarios()) {
                System.out.println("  Comentario: ID=" + comentario.id() + ", Usuario=" + comentario.usuarioId() +
                        ", Contenido=" + comentario.contenidoTexto() + ", Likes=" + comentario.likes() + ", Fecha=" + comentario.fecha());
            }
        }

        // 2. Crear un nuevo post
        System.out.println("\n=== Crear un nuevo post ===");
        CrearPostRequest nuevoPostRequest = new CrearPostRequest(1, "Nuevo tema: ¿Cómo optimizar JavaFX?", "Optimización JavaFX");
        PostResponse nuevoPost = foroService.crearPost(nuevoPostRequest);
        System.out.println("Post creado: ID=" + nuevoPost.id() + ", Usuario=" + nuevoPost.usuarioId() + ", Contenido=" +
                nuevoPost.contenido() + ", Likes=" + nuevoPost.likes() + ", Etiqueta=" + nuevoPost.etiqueta() +
                ", Fecha=" + nuevoPost.fechaCreacion());

        // 3. Crear un nuevo comentario para el post recién creado, el mismo usuario agrega un comentario a su post
        System.out.println("\n=== Crear un nuevo comentario ===");
        CrearComentarioRequest nuevoComentarioRequest = new CrearComentarioRequest(nuevoPost.id(), 1, "Usa perfiles de rendimiento.");
        ComentarioResponse nuevoComentario = foroService.crearComentario(nuevoComentarioRequest);
        System.out.println("Comentario creado: ID=" + nuevoComentario.id() + ", Usuario=" + nuevoComentario.usuarioId() +
                ", Contenido=" + nuevoComentario.contenidoTexto() + ", Likes=" + nuevoComentario.likes() + ", Fecha=" + nuevoComentario.fecha());


        // 4. Dar like al post existente (ID=1 del INSERT inicial)
        System.out.println("\n=== Dar like al post (ID=1) ===");
        PostResponse postConLike = foroService.darLikeAPost(1);
        System.out.println("Post después de like: ID=" + postConLike.id() + ", Likes=" + postConLike.likes() +
                ", Fecha=" + postConLike.fechaCreacion());

        // 5. Dar like al comentario existente (ID=1 del INSERT inicial)
        System.out.println("\n=== Dar like al comentario existente (ID=1) ===");
        ComentarioResponse comentarioConLike = foroService.darLikeAComentario(1);
        System.out.println("Comentario después de like: ID=" + comentarioConLike.id() + ", Likes=" + comentarioConLike.likes() +
                ", Fecha=" + comentarioConLike.fecha());

        // 6. Obtener el post por ID para verificar
        System.out.println("\n=== Obtener post por ID (ID=1) ===");
        PostResponse postPorId = foroService.obtenerPostPorId(1);
        System.out.println("Post: ID=" + postPorId.id() + ", Usuario=" + postPorId.usuarioId() + ", Contenido=" + postPorId.contenido() +
                ", Likes=" + postPorId.likes() + ", Etiqueta=" + postPorId.etiqueta() + ", Fecha=" + postPorId.fechaCreacion());
        for (ComentarioResponse comentario : postPorId.comentarios()) {
            System.out.println("  Comentario: ID=" + comentario.id() + ", Usuario=" + comentario.usuarioId() +
                    ", Contenido=" + comentario.contenidoTexto() + ", Likes=" + comentario.likes() + ", Fecha=" + comentario.fecha());
        }




    }
}
