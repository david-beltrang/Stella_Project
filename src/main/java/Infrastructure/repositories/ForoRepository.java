
package Infrastructure.repositories;

import Domain.models.Post;
import Domain.models.Comentario;
import Domain.models.ForoValueObjects.Contenido;

import Application.dtos.foro.*;
import Domain.repositoriesInterfaces.InterfazForoRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ForoRepository implements InterfazForoRepository {
    private final IConexionBD connMgr;

    public ForoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public List<PostResponse> obtenerTodos() {
        List<PostResponse> posts = new ArrayList<>();
        String sql = """
        SELECT p.id, p.usuario_id, p.contenido_texto, p.likes, p.fecha, p.etiqueta,
               c.id AS comentario_id, c.usuario_id AS c_usuario_id, c.contenido_texto AS c_contenido, 
               c.fecha AS c_fecha, c.likes AS c_likes
        FROM "post" p
        LEFT JOIN "comentario" c ON c.post_id = p.id
        ORDER BY p.id, c.id
        """;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            PostResponse currentPost = null;
            List<ComentarioResponse> comentarios = new ArrayList<>();

            while (rs.next()) {
                int postId = rs.getInt("id");

                // Si cambió el post, guardamos el anterior
                if (currentPost == null || currentPost.id() != postId) {
                    // Guardar el post anterior (si existe)
                    if (currentPost != null) {
                        posts.add(new PostResponse(
                                currentPost.id(),
                                currentPost.usuarioId(),
                                currentPost.contenido(),
                                currentPost.likes(),
                                currentPost.fechaCreacion(),
                                currentPost.etiqueta(),
                                new ArrayList<>(comentarios)
                        ));
                        comentarios.clear();
                    }

                    // Crear nuevo post
                    currentPost = new PostResponse(
                            postId,
                            rs.getInt("usuario_id"),
                            rs.getString("contenido_texto"),
                            rs.getInt("likes"),
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getString("etiqueta"),
                            new ArrayList<>() // comentarios vacíos por ahora
                    );
                }

                // Agregar comentario si existe
                int comentarioId = rs.getInt("comentario_id");
                if (!rs.wasNull()) {
                    comentarios.add(new ComentarioResponse(
                            comentarioId,
                            rs.getInt("c_usuario_id"),
                            rs.getString("c_contenido"),
                            rs.getTimestamp("c_fecha").toLocalDateTime(),
                            rs.getInt("c_likes")
                    ));
                }
            }

            // No olvidar agregar el último post
            if (currentPost != null) {
                posts.add(new PostResponse(
                        currentPost.id(),
                        currentPost.usuarioId(),
                        currentPost.contenido(),
                        currentPost.likes(),
                        currentPost.fechaCreacion(),
                        currentPost.etiqueta(),
                        new ArrayList<>(comentarios)
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener posts", e);
        }
        return posts;
    }

    @Override
    public PostResponse obtenerPostResponsePorId(int id) {
        String sql = """
        SELECT p.id, p.usuario_id, p.contenido_texto, p.likes, p.fecha, p.etiqueta,
               c.id AS comentario_id, c.usuario_id AS c_usuario_id, c.contenido_texto AS c_contenido, 
               c.fecha AS c_fecha, c.likes AS c_likes
        FROM "post" p
        LEFT JOIN "comentario" c ON c.post_id = p.id
        WHERE p.id = ?
        ORDER BY c.id
        """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null; // No se encontró el post
                }

                // Leer los datos del post de la primera fila
                int postId = rs.getInt("id");
                int usuarioId = rs.getInt("usuario_id");
                String contenidoTexto = rs.getString("contenido_texto");
                int likes = rs.getInt("likes");
                LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
                String etiqueta = rs.getString("etiqueta");

                List<ComentarioResponse> comentarios = new ArrayList<>();
                int currentComentarioId = rs.getInt("comentario_id");
                if (!rs.wasNull()) {
                    comentarios.add(new ComentarioResponse(
                            currentComentarioId,
                            rs.getInt("c_usuario_id"),
                            rs.getString("c_contenido"),
                            rs.getTimestamp("c_fecha").toLocalDateTime(),
                            rs.getInt("c_likes")
                    ));
                }

                // Iterar sobre las filas restantes para los comentarios
                while (rs.next()) {
                    currentComentarioId = rs.getInt("comentario_id");
                    if (!rs.wasNull()) {
                        comentarios.add(new ComentarioResponse(
                                currentComentarioId,
                                rs.getInt("c_usuario_id"),
                                rs.getString("c_contenido"),
                                rs.getTimestamp("c_fecha").toLocalDateTime(),
                                rs.getInt("c_likes")
                        ));
                    }
                }

                // Retornar el PostResponse con los datos del post y los comentarios
                return new PostResponse(postId, usuarioId, contenidoTexto, likes, fecha, etiqueta, comentarios);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener post por ID", e);
        }
    }

    @Override
    public Post obtenerPostPorId(int id) {
        String sql = "SELECT * FROM \"post\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Post.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            new Contenido(rs.getString("contenido_texto")),
                            rs.getInt("likes"),
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getString("etiqueta")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener post por ID", e);
        }
        throw new RuntimeException("Post no encontrado");
    }

    public Comentario obtenerComentarioPorId(int id) {
        String sql = "SELECT * FROM \"comentario\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Comentario.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("post_id"),
                            rs.getInt("usuario_id"),
                            new Contenido(rs.getString("contenido_texto")),
                            rs.getInt("likes"),
                            rs.getTimestamp("fecha").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener post por ID", e);
        }
        throw new RuntimeException("Post no encontrado");
    }

    public ComentarioResponse obtenerComentarioResponsePorId(int id) {
        String sql = "SELECT * FROM \"comentario\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ComentarioResponse(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getString("contenido_texto"),
                            rs.getTimestamp("fecha").toLocalDateTime(),
                            rs.getInt("likes")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener comentario por ID", e);
        }
        throw new RuntimeException("Comentario no encontrado");
    }

    @Override
    public Post guardarPost(Post post) {
        String sql = "INSERT INTO \"post\" (usuario_id, contenido_texto, fecha, etiqueta) VALUES (?, ?, ?, ?)";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, post.getUsuarioId());
            pstmt.setString(2, post.getContenido().texto());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, post.getEtiqueta());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No se insertó ningún registro.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1); // Obtener el ID generado (columna 1)
                    return Post.reconstruir(
                            generatedId,
                            post.getUsuarioId(),
                            post.getContenido(),
                            0,
                            LocalDateTime.now(),
                            post.getEtiqueta()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar post", e);
        }
        throw new RuntimeException("No se pudo obtener el ID del post");
    }

    @Override
    public Comentario guardarComentario(Comentario comentario) {
        String sql = "INSERT INTO \"comentario\" (post_id, usuario_id, contenido_texto, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, comentario.getPostId());
            pstmt.setInt(2, comentario.getUsuarioId());
            pstmt.setString(3, comentario.getContenido().texto());
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("No se insertó ningún registro.");
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1); // Obtener el ID generado (columna 1)
                    return Comentario.reconstruir(
                            generatedId,
                            comentario.getPostId(),
                            comentario.getUsuarioId(),
                            comentario.getContenido(),
                            0,
                            LocalDateTime.now()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar comentario", e);
        }
        throw new RuntimeException("No se pudo obtener el ID del comentario");
    }

    @Override
    public void actualizarLikesPost(int postId, int likes) {
        String sql = "UPDATE \"post\" SET likes = ? WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, likes);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar likes", e);
        }
    }

    @Override
    public void actualizarLikesComentario(int comentario_id, int likes) {
        String sql = "UPDATE \"comentario\" SET likes = ? WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, likes);
            pstmt.setInt(2, comentario_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar likes", e);
        }
    }
}