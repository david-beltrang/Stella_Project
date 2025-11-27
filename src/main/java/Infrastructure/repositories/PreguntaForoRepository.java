package Infrastructure.repositories;

import Domain.models.PreguntaForo;
import Domain.repositoriesInterfaces.InterfazPreguntaForoRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PreguntaForoRepository implements InterfazPreguntaForoRepository {

    private final IConexionBD connMgr;

    public PreguntaForoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void guardar(PreguntaForo pregunta) {
        String sql = pregunta.getId() == null
                ? "INSERT INTO \"pregunta_foro\" (usuario_id, titulo, contenido) VALUES (?, ?, ?)"
                : "UPDATE \"pregunta_foro\" SET titulo=?, contenido=? WHERE id=?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (pregunta.getId() == null) {
                stmt.setInt(1, pregunta.getUsuarioId());
                stmt.setString(2, pregunta.getTitulo());
                stmt.setString(3, pregunta.getContenido());
            } else {
                stmt.setString(1, pregunta.getTitulo());
                stmt.setString(2, pregunta.getContenido());
                stmt.setInt(3, pregunta.getId());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar pregunta", e);
        }
    }

    @Override
    public Optional<PreguntaForo> obtenerPorId(int id) {
        String sql = "SELECT id, usuario_id, titulo, contenido, fecha_creacion FROM \"pregunta_foro\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearPregunta(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener pregunta", e);
        }
        return Optional.empty();
    }

    @Override
    public List<PreguntaForo> listarTodas() {
        String sql = "SELECT id, usuario_id, titulo, contenido, fecha_creacion FROM \"pregunta_foro\" ORDER BY fecha_creacion DESC";
        List<PreguntaForo> preguntas = new ArrayList<>();

        try (Connection conn = connMgr.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                preguntas.add(mapearPregunta(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar preguntas", e);
        }
        return preguntas;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM \"pregunta_foro\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar pregunta", e);
        }
    }

    private PreguntaForo mapearPregunta(ResultSet rs) throws SQLException {
        return PreguntaForo.reconstruir(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getString("titulo"),
                rs.getString("contenido"),
                rs.getTimestamp("fecha_creacion").toLocalDateTime());
    }
}
