package Infrastructure.repositories;

import Domain.models.ProgresoLeccion;
import Domain.models.LeccionValueObjects.EstadoLeccion;
import Domain.repositoriesInterfaces.InterfazProgresoLeccionRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.util.Optional;

// Implementación JDBC real del repositorio de ProgresoLeccion.
public class ProgresoLeccionRepository implements InterfazProgresoLeccionRepository {

    private static final String SQL_SELECT_BY_USER_AND_LECCION =
            "SELECT id, usuario_id, leccion_id, estado, fecha_inicio, fecha_completado FROM progreso_leccion WHERE usuario_id = ? AND leccion_id = ?";
    private static final String SQL_INSERT =
            "INSERT INTO progreso_leccion (usuario_id, leccion_id, estado, fecha_inicio, fecha_completado) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE =
            "UPDATE progreso_leccion SET estado = ?, fecha_inicio = ?, fecha_completado = ? WHERE id = ?";
    private static final String SQL_UPDATE_ESTADO =
            "UPDATE progreso_leccion SET estado = ? WHERE id = ?";

    // Método para obtener el progreso de un usuario y lección.
    @Override
    public Optional<ProgresoLeccion> buscarPorUsuarioYLeccion(int usuarioId, int leccionId) {
        ProgresoLeccion progreso = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_USER_AND_LECCION)) {

            ps.setInt(1, usuarioId);
            ps.setInt(2, leccionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    progreso = mapearProgreso(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar progreso: " + e.getMessage());
        }
        return Optional.ofNullable(progreso);
    }

    // Método para guardar el progreso de un usuario y lección.
    @Override
    public ProgresoLeccion guardar(ProgresoLeccion progreso) {
        if (progreso.getId() == null) {
            return insertar(progreso);
        } else {
            return actualizar(progreso);
        }
    }

    // Método para actualizar el estado de un progreso.
    @Override
    public void actualizarEstado(int progresoId, EstadoLeccion nuevoEstado) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_ESTADO)) {

            ps.setString(1, nuevoEstado.valor());
            ps.setInt(2, progresoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado directo: " + e.getMessage());
        }
    }

    // Método para insertar un nuevo progreso.
    private ProgresoLeccion insertar(ProgresoLeccion progreso) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, progreso.getUsuarioId());
            ps.setInt(2, progreso.getLeccionId());
            ps.setString(3, progreso.getEstado().valor());
            ps.setTimestamp(4, progreso.getFechaInicio() != null ? Timestamp.valueOf(progreso.getFechaInicio()) : null);
            ps.setTimestamp(5, progreso.getFechaCompletado() != null ? Timestamp.valueOf(progreso.getFechaCompletado()) : null);

            ps.executeUpdate();

            // Obtener el ID generado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    Integer newId = rs.getInt(1);
                    return ProgresoLeccion.reconstruir(
                            newId,
                            progreso.getUsuarioId(),
                            progreso.getLeccionId(),
                            progreso.getEstado().valor(),
                            progreso.getFechaInicio(),
                            progreso.getFechaCompletado()
                    );
                }
            }
            throw new SQLException("Fallo al insertar progreso, no se obtuvo ID generado.");

        } catch (SQLException e) {
            System.err.println("Error al insertar progreso: " + e.getMessage());
            throw new RuntimeException("Error de persistencia al guardar ProgresoLeccion", e);
        }
    }

    // Método para actualizar un progreso existente.
    private ProgresoLeccion actualizar(ProgresoLeccion progreso) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, progreso.getEstado().valor());
            ps.setTimestamp(2, progreso.getFechaInicio() != null ? Timestamp.valueOf(progreso.getFechaInicio()) : null);
            ps.setTimestamp(3, progreso.getFechaCompletado() != null ? Timestamp.valueOf(progreso.getFechaCompletado()) : null);
            ps.setInt(4, progreso.getId());

            ps.executeUpdate();
            return progreso;
        } catch (SQLException e) {
            System.err.println("Error al actualizar progreso: " + e.getMessage());
            throw new RuntimeException("Error de persistencia al actualizar ProgresoLeccion", e);
        }
    }

    // Mapea un ResultSet a la entidad ProgresoLeccion (usado por ProgresoLeccionService).
    private ProgresoLeccion mapearProgreso(ResultSet rs) throws SQLException {
        return ProgresoLeccion.reconstruir(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getInt("leccion_id"),
                rs.getString("estado"),
                rs.getTimestamp("fecha_inicio") != null ? rs.getTimestamp("fecha_inicio").toLocalDateTime() : null,
                rs.getTimestamp("fecha_completado") != null ? rs.getTimestamp("fecha_completado").toLocalDateTime() : null
        );
    }
}