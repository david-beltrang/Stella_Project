package Infrastructure.repositories;

import Domain.models.SesionEstudio;
import Domain.models.PomodoroValueObjects.TiempoDescanso;
import Domain.models.PomodoroValueObjects.TiempoEstudio;
import Domain.repositoriesInterfaces.InterfazSesionEstudioRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SesionEstudioRepository implements InterfazSesionEstudioRepository {
    private IConexionBD connMgr;

    public SesionEstudioRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }
    @Override
    public List<SesionEstudio> buscarPorUsuarioYRangoFecha(
            int usuarioId,
            Timestamp inicio,
            Timestamp fin
    ) {

        String sql = """
        SELECT * FROM "sesion_estudio"
        WHERE usuario_id = ?
        AND fecha_inicio BETWEEN ? AND ?
        ORDER BY fecha_inicio ASC
    """;

        List<SesionEstudio> resultados = new ArrayList<>();

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setTimestamp(2, inicio);
            pstmt.setTimestamp(3, fin);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapRowToSesionEstudio(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sesiones por rango de fechas", e);
        }

        return resultados;
    }

    @Override
    public SesionEstudio guardar(SesionEstudio sesion) {
        try (Connection conn = connMgr.getConnection()) {
            // Si el id es null, es una nueva sesión (inserción)
            if (sesion.getId() == null) {
                String insertSql = "INSERT INTO \"sesion_estudio\" (usuario_id, tiempo_estudio, tiempo_descanso, fecha_inicio, fecha_final) " +
                        "VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    pstmt.setInt(1, sesion.getUsuarioId());
                    pstmt.setInt(2, sesion.getTiempoEstudio().minutos());
                    pstmt.setInt(3, sesion.getTiempoDescanso().minutos());
                    pstmt.setTimestamp(4, sesion.getFechaInicio());
                    pstmt.setTimestamp(5, sesion.getFechaFinal());
                    pstmt.executeUpdate();

                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            //usamos el método para reconstruir la sesion y devolverla para implementaciones futuras
                            return SesionEstudio.reconstruir(
                                    generatedId,
                                    sesion.getUsuarioId(),
                                    new TiempoEstudio(sesion.getTiempoEstudio().minutos()), //Como es un record es necesario acceder al valor de los minutos
                                    new TiempoDescanso(sesion.getTiempoDescanso().minutos()), //Como es un record es necesario acceder al valor de los minutos
                                    sesion.getFechaInicio(),
                                    sesion.getFechaFinal()
                            );
                        }
                        throw new SQLException("No se generó ID para la sesión.");
                    }
                }
            } else {
                // Si el id existe, es un update es decir que hay una finalización de sesión, ya que anteriormente se metió la sesión de inicio en la BD
                String updateSql = "UPDATE \"sesion_estudio\" SET fecha_final = ?, tiempo_estudio = ?, tiempo_descanso = ? WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setTimestamp(1, sesion.getFechaFinal());
                    pstmt.setInt(2, sesion.getTiempoEstudio().minutos());
                    pstmt.setInt(3, sesion.getTiempoDescanso().minutos());
                    pstmt.setInt(4, sesion.getId());
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("No se encontró la sesión con id " + sesion.getId() + " para actualizar.");
                    }
                    return sesion; // Devuelve la sesión actualizada
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando sesión: " + e.getMessage(), e);
        }
    }

    @Override
    public SesionEstudio encontrarPorId(int id) {
        String sql = "SELECT * FROM \"sesion_estudio\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSesionEstudio(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando sesión por ID: " + e.getMessage(), e);
        }
        return null;
    }

    // Método auxiliarpara poder devolver la sesion
    private SesionEstudio mapRowToSesionEstudio(ResultSet rs) throws SQLException {
        return SesionEstudio.reconstruir(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                new TiempoEstudio(rs.getInt("tiempo_estudio")),
                new TiempoDescanso(rs.getInt("tiempo_descanso")),
                rs.getTimestamp("fecha_inicio"),
                rs.getTimestamp("fecha_final")
        );
    }
}