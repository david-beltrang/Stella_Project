
package Infrastructure.repositories;

import Domain.models.Post;
import Domain.models.Comentario;
import Domain.models.ForoValueObjects.Contenido;

import Application.dtos.Foro.*;
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
            SELECT p.id, p.usuario_id, p.contenido_texto, p.likes, p.fecha, 
                   c.id AS comentario_id, c.usuario_id AS c_usuario_id, c.contenido_texto AS c_contenido, 
                   c.imagen_path, c.fecha AS c_fecha, c.likes AS c_likes
            FROM \"post\" p
            LEFT JOIN \"comentario\" c ON c.post_id = p.id
            ORDER BY p.id, c.id
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            int currentPostId = -1;
            List<ComentarioResponse> comentarios = new ArrayList<>();
            while (rs.next()) {
                int postId = rs.getInt("id");
                if (postId != currentPostId) {
                    if (currentPostId != -1) {
                        posts.add(new PostResponse(currentPostId, rs.getInt("usuario_id"), rs.getString("contenido_texto"),
                                rs.getInt("likes"), rs.getTimestamp("fecha").toLocalDateTime(), new ArrayList<>(comentarios)));
                        comentarios.clear();
                    }
                    currentPostId = postId;
                }
                int comentarioId = rs.getInt("comentario_id");
                if (rs.wasNull()) continue;
                comentarios.add(new ComentarioResponse(
                        comentarioId,
                        rs.getInt("c_usuario_id"),
                        rs.getString("c_contenido"),
                        rs.getTimestamp("c_fecha").toLocalDateTime(),
                        rs.getInt("c_likes")
                ));
            }
            if (currentPostId != -1) {
                posts.add(new PostResponse(currentPostId, rs.getInt("usuario_id"), rs.getString("contenido_texto"),
                        rs.getInt("likes"), rs.getTimestamp("fecha").toLocalDateTime(), new ArrayList<>(comentarios)));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener posts", e);
        }
        return posts;
    }

    @Override
    public PostResponse obtenerPostResponsePorId(int id) {
        String sql = """
            SELECT p.id, p.usuario_id, p.contenido_texto, p.likes, p.fecha, 
                   c.id AS comentario_id, c.usuario_id AS c_usuario_id, c.contenido_texto AS c_contenido, 
                   c.imagen_path, c.fecha AS c_fecha, c.likes AS c_likes
            FROM \"post\" p
            LEFT JOIN \"comentario\" c ON c.post_id = p.id
            WHERE p.id = ?
            ORDER BY c.id
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                int postId = rs.getInt("id");
                List<ComentarioResponse> comentarios = new ArrayList<>();
                do {
                    int comentarioId = rs.getInt("comentario_id");
                    if (rs.wasNull()) break;
                    comentarios.add(new ComentarioResponse(
                            comentarioId,
                            rs.getInt("c_usuario_id"),
                            rs.getString("c_contenido"),
                            rs.getTimestamp("c_fecha").toLocalDateTime(),
                            rs.getInt("c_likes")
                    ));
                } while (rs.next());
                return new PostResponse(
                        postId,
                        rs.getInt("usuario_id"),
                        rs.getString("contenido_texto"),
                        rs.getInt("likes"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        comentarios
                );
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
                            rs.getTimestamp("fecha").toLocalDateTime()
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
        String sql = "INSERT INTO \"post\" (usuario_id, contenido_texto, fecha) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, post.getUsuarioId());
            pstmt.setString(2, post.getContenido().texto());
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Post.reconstruir(
                            rs.getInt("id"),
                            post.getUsuarioId(),
                            post.getContenido(),
                            0,
                            LocalDateTime.now()
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
        String sql = "INSERT INTO \"comentario\" (post_id, usuario_id, contenido_texto, fecha) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, comentario.getPostId());
            pstmt.setInt(2, comentario.getUsuarioId());
            pstmt.setString(3, comentario.getContenido().texto());
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Comentario.reconstruir(
                            rs.getInt("id"),
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