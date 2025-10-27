package Infrastructure.repositories;

import Domain.models.LeccionValueObjects.EstadoProgreso;
import Domain.models.ProgresoLeccion;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.time.Instant;

// Implementación JDBC real del repositorio de ProgresoLeccion.
public class ProgresoRepository implements InterfazProgresoRepository {

    private IConexionBD connMgr;

    public ProgresoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    private ProgresoLeccion mapResultSetToProgreso(ResultSet rs) throws SQLException {

        // Obtenemos los Timestamps de forma segura
        java.sql.Timestamp tsInicio = rs.getTimestamp("fecha_inicio");
        java.sql.Timestamp tsCompletado = rs.getTimestamp("fecha_completado");

        // Lógica para convertir a Instant, manejando la nulidad
        Instant fechaInicio = tsInicio != null ? tsInicio.toInstant() : null;
        Instant fechaCompletado = tsCompletado != null ? tsCompletado.toInstant() : null;

        return new ProgresoLeccion(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getInt("leccion_id"),
                EstadoProgreso.valueOf(rs.getString("estado")),
                fechaInicio,        // Usamos la variable segura
                fechaCompletado     // Usamos la variable segura
        );
    }

    @Override
    public ProgresoLeccion buscarPorUsuarioYLeccion(Integer usuarioId, Integer leccionId) {
        String sql = "SELECT * FROM progreso_leccion WHERE usuario_id = ? AND leccion_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setInt(2, leccionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProgreso(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar progreso: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ProgresoLeccion guardar(ProgresoLeccion progreso) {
        // Lógica para INSERT o UPDATE
        String sql = "INSERT INTO progreso_leccion (usuario_id, leccion_id, estado, fecha_inicio, fecha_completado) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE estado=?, fecha_completado=?";
        // Lógica de conexión, seteo de parámetros y ejecución.
        return progreso;
    }
}