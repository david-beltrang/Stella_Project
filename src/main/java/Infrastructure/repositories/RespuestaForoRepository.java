package Infrastructure.repositories;

import Domain.models.RespuestaForo;
import Domain.repositoriesInterfaces.InterfazRespuestaForoRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RespuestaForoRepository implements InterfazRespuestaForoRepository {

    private final IConexionBD connMgr;

    public RespuestaForoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void guardar(RespuestaForo respuesta) {
        String sql = "INSERT INTO \"respuesta_foro\" (pregunta_id, usuario_id, contenido) VALUES (?, ?, ?)";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, respuesta.getPreguntaId());
            stmt.setInt(2, respuesta.getUsuarioId());
            stmt.setString(3, respuesta.getContenido());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar respuesta", e);
        }
    }

    @Override
    public Optional<RespuestaForo> obtenerPorId(int id) {
        String sql = "SELECT id, pregunta_id, usuario_id, contenido, fecha_creacion FROM \"respuesta_foro\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRespuesta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener respuesta", e);
        }
        return Optional.empty();
    }

    @Override
    public List<RespuestaForo> listarPorPregunta(int preguntaId) {
        String sql = "SELECT id, pregunta_id, usuario_id, contenido, fecha_creacion FROM \"respuesta_foro\" WHERE pregunta_id = ? ORDER BY fecha_creacion ASC";
        List<RespuestaForo> respuestas = new ArrayList<>();

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, preguntaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    respuestas.add(mapearRespuesta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar respuestas", e);
        }
        return respuestas;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM \"respuesta_foro\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar respuesta", e);
        }
    }

    private RespuestaForo mapearRespuesta(ResultSet rs) throws SQLException {
        return RespuestaForo.reconstruir(
                rs.getInt("id"),
                rs.getInt("pregunta_id"),
                rs.getInt("usuario_id"),
                rs.getString("contenido"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime());
    }
}
