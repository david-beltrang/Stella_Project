// src/test/java/Application/services/ForoService_IntegrationTest.java
package Application.services;

import Application.dtos.foro.*;
import Domain.repositoriesInterfaces.InterfazForoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.ForoRepository;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ForoService - Tests de integración reales (CC total: 15)")
class ForoService_IntegrationTest {

    private ForoService service;
    private InterfazForoRepository foroRepo;

    @BeforeEach
    void setUp() {
        // BD limpia + todos los datos reales del foro (2 posts, 3 comentarios, 2 usuarios)
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        foroRepo = new ForoRepository(ConexionBD.getInstance());
        service = new ForoService(foroRepo);
    }

    // ========================================================================
    // 1. obtenerTodosLosPosts() → Complejidad ciclomatica = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerTodosLosPosts - Devuelve 2 posts con comentarios anidados correctamente")
    void obtenerTodosLosPosts_RetornaEstructuraCompleta() {
        List<PostResponse> posts = service.obtenerTodosLosPosts();

        assertEquals(2, posts.size());

        // Post 1 (JavaFX)
        PostResponse post1 = posts.get(0);
        assertEquals(1, post1.id());
        assertEquals(1, post1.usuarioId());
        assertEquals("¿Alguien sabe cómo usar JavaFX para un foro?", post1.contenido());
        assertEquals("JavaFX", post1.etiqueta());
        assertEquals(2, post1.comentarios().size());
        assertEquals("Sí, usa FXML para la interfaz.", post1.comentarios().get(0).contenidoTexto());

        // Post 2 (C++)
        PostResponse post2 = posts.get(1);
        assertEquals(2, post2.id());
        assertEquals(2, post2.usuarioId());
        assertEquals("¿Alguien sabe cómo utilizar la memoria dinámica en c++?", post2.contenido());
        assertEquals(1, post2.comentarios().size());
        assertEquals("Sí, debes asignar y liberar memoria así: int *arr = new int[10]; y delete[] arr;",
                post2.comentarios().get(0).contenidoTexto());
    }

    // ========================================================================
    // 2. obtenerPostPorId(int id) → Complejidad ciclomatica = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerPostPorId - Post existente → devuelve con comentarios")
    void obtenerPostPorId_Existente_RetornaCompleto() {
        PostResponse post = service.obtenerPostPorId(1);
        assertNotNull(post);
        assertEquals(1, post.id());
        assertEquals(2, post.comentarios().size());
    }

    @Test
    @DisplayName("obtenerPostPorId - Post inexistente → lanza RuntimeException")
    void obtenerPostPorId_Inexistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> service.obtenerPostPorId(999));
    }

    // ========================================================================
    // 3. crearPost(CrearPostRequest request) → Complejidad ciclomatica = 4
    // ========================================================================
    @Test
    @DisplayName("crearPost - Válido → crea post y aparece en lista")
    void crearPost_Valido_CreaCorrectamente() {
        var request = new CrearPostRequest(1, "Nuevo post de prueba", "Duda");

        PostResponse creado = service.crearPost(request);

        assertTrue(creado.id() > 2); // Nuevo ID
        assertEquals("Nuevo post de prueba", creado.contenido());
        assertEquals("Duda", creado.etiqueta());
        assertEquals(0, creado.likes());
        assertEquals(1, creado.usuarioId());

        // Verificar que aparece en la lista general
        List<PostResponse> todos = service.obtenerTodosLosPosts();
        assertTrue(todos.stream().anyMatch(p -> p.id() == creado.id()));
    }

    @Test
    @DisplayName("crearPost - Contenido vacío → lanza IllegalArgumentException")
    void crearPost_ContenidoVacio_LanzaExcepcion() {
        var request = new CrearPostRequest(1, "", "Duda");
        assertThrows(IllegalArgumentException.class, () -> service.crearPost(request));
    }

    // ========================================================================
    // 4. crearComentario(CrearComentarioRequest request) → Complejidad ciclomatica = 5
    // ========================================================================
    @Test
    @DisplayName("crearComentario - Válido → agrega comentario al post")
    void crearComentario_Valido_AgregaCorrectamente() {
        var request = new CrearComentarioRequest(1, 1, "Este es un nuevo comentario");

        ComentarioResponse comentario = service.crearComentario(request);

        assertTrue(comentario.id() > 3);
        assertEquals(1, comentario.usuarioId());
        assertEquals("Este es un nuevo comentario", comentario.contenidoTexto());

        // Verificar que aparece en el post
        PostResponse postActualizado = service.obtenerPostPorId(1);
        assertEquals(3, postActualizado.comentarios().size());
        assertTrue(postActualizado.comentarios().stream()
                .anyMatch(c -> c.contenidoTexto().contains("nuevo comentario")));
    }

    @Test
    @DisplayName("crearComentario - Post inexistente → lanza RuntimeException")
    void crearComentario_PostInexistente_LanzaExcepcion() {
        var request = new CrearComentarioRequest(999, 1, "Comentario inválido");
        assertThrows(RuntimeException.class, () -> service.crearComentario(request));
    }

    // ========================================================================
    // 5. darLikeAPost(int postId, int usuarioId) → Complejidad ciclomatica = 2
    // ========================================================================
    @Test
    @DisplayName("darLikeAPost - Post existente → incrementa likes")
    void darLikeAPost_Valido_IncrementaLikes() {
        PostResponse antes = service.obtenerPostPorId(1);
        int likesAntes = antes.likes();

        service.darLikeAPost(1);

        PostResponse despues = service.obtenerPostPorId(1);
        assertEquals(likesAntes + 1, despues.likes());
    }

    @Test
    @DisplayName("darLikeAPost - Post inexistente → lanza RuntimeException")
    void darLikeAPost_Inexistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> service.darLikeAPost(999));
    }

    // ========================================================================
    // 6. darLikeAComentario(int comentarioId, int usuarioId) → Complejidad ciclomatica = 2
    // ========================================================================
    @Test
    @DisplayName("darLikeAComentario - Comentario existente → incrementa likes")
    void darLikeAComentario_Valido_IncrementaLikes() {
        // El comentario 1 existe (del post 1)
        PostResponse post = service.obtenerPostPorId(1);
        int likesAntes = post.comentarios().get(0).likes();

        service.darLikeAComentario(1);

        PostResponse actualizado = service.obtenerPostPorId(1);
        int likesDespues = actualizado.comentarios().get(0).likes();
        assertEquals(likesAntes + 1, likesDespues);
    }

    @Test
    @DisplayName("darLikeAComentario - Comentario inexistente → lanza RuntimeException")
    void darLikeAComentario_Inexistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () -> service.darLikeAComentario(999));
    }
}