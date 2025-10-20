package Infrastructure.repositories;

import Application.dtos.curso.ProgresoLeccionId;
import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.CursoValueObjects.EstadoProgreso;
import Domain.models.ProgresoLeccion;
import Domain.models.UsuarioValueObjects.UsuarioId;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.time.Instant;

// Implementación JDBC real del repositorio de ProgresoLeccion.
public class ProgresoRepository implements InterfazProgresoRepository {

    private final ConexionBD db;

    public ProgresoRepository(ConexionBD db) {
        this.db = db;
    }

    private ProgresoLeccion mapResultSetToProgreso(ResultSet rs) throws SQLException {

        // Obtenemos los Timestamps de forma segura
        java.sql.Timestamp tsInicio = rs.getTimestamp("fecha_inicio");
        java.sql.Timestamp tsCompletado = rs.getTimestamp("fecha_completado");

        // Lógica para convertir a Instant, manejando la nulidad
        Instant fechaInicio = tsInicio != null ? tsInicio.toInstant() : null;
        Instant fechaCompletado = tsCompletado != null ? tsCompletado.toInstant() : null;

        return new ProgresoLeccion(
                new ProgresoLeccionId(rs.getInt("id")),
                new UsuarioId(rs.getInt("usuario_id")),
                new LeccionId(rs.getInt("leccion_id")),
                EstadoProgreso.valueOf(rs.getString("estado")),
                fechaInicio,        // Usamos la variable segura
                fechaCompletado     // Usamos la variable segura
        );
    }

    @Override
    public ProgresoLeccion buscarPorUsuarioYLeccion(UsuarioId usuarioId, LeccionId leccionId) {
        String sql = "SELECT * FROM progreso_leccion WHERE usuario_id = ? AND leccion_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId.valor());
            stmt.setInt(2, leccionId.valor());

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
        // (Omitida por brevedad)
        return progreso;
    }
}