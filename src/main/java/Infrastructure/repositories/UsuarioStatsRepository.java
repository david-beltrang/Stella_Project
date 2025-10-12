package Infrastructure.repositories;

import Domain.repositoriesInterfaces.InterfazUsuarioStatsRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Implementación JDBC para la persistencia de las estadísticas de usuario (racha, pescaditos).
 */
public class UsuarioStatsRepository implements InterfazUsuarioStatsRepository {

    // CORRECCIÓN FINAL: Usamos comillas dobles para forzar el nombre "dias_racha"
    private static final String SQL_UPDATE_RACHA =
            "UPDATE usuario_stats SET racha_dias = racha_dias + ? WHERE usuario_id = ?";
    private static final String SQL_UPDATE_PESCADITOS =
            "UPDATE usuario_stats SET pescaditos = pescaditos + ? WHERE usuario_id = ?";

    @Override
    public void actualizarRacha(int usuarioId, int diasSumar) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_RACHA)) {

            ps.setInt(1, diasSumar);
            ps.setInt(2, usuarioId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                // Podría indicar que el usuarioId aún no tiene una fila en usuario_stats
                System.err.println("Advertencia: No se encontró registro de stats para actualizar racha. La fila debe ser creada primero.");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar racha: " + e.getMessage());
            throw new RuntimeException("Error de persistencia al actualizar Racha", e);
        }
    }

    @Override
    public void actualizarPescaditos(int usuarioId, int cantidadSumar) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PESCADITOS)) {

            ps.setInt(1, cantidadSumar);
            ps.setInt(2, usuarioId);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar pescaditos: " + e.getMessage());
        }
    }
}