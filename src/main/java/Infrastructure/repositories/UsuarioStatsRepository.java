package Infrastructure.repositories;

import Domain.repositoriesInterfaces.InterfazUsuarioStatsRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.IConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación JDBC para la persistencia de las estadísticas de usuario (racha, pescaditos).
 */
public class UsuarioStatsRepository implements InterfazUsuarioStatsRepository {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioStatsRepository.class);
    private IConexionBD connMgr;

    // Usar comillas dobles para forzar el nombre "dias_racha"
    private static final String SQL_UPDATE_RACHA =
            "UPDATE usuario_stats SET racha_dias = racha_dias + ? WHERE usuario_id = ?";
    private static final String SQL_UPDATE_PESCADITOS =
            "UPDATE usuario_stats SET pescaditos = pescaditos + ? WHERE usuario_id = ?";

    public UsuarioStatsRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    // Método para actualizar la racha de días de estudio de un usuario.
    @Override
    public void actualizarRacha(int usuarioId, int diasSumar) {
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_RACHA)) {

            ps.setInt(1, diasSumar);
            ps.setInt(2, usuarioId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                // Podría indicar que el usuarioId aún no tiene una fila en usuario_stats
                logger.warn("No se encontró registro de stats para actualizar racha del usuario {}. La fila debe ser creada primero.", usuarioId);
            }

        } catch (SQLException e) {
            logger.error("Error al actualizar racha del usuario {}", usuarioId, e);
            throw new RuntimeException("Error de persistencia al actualizar Racha", e);
        }
    }

    // Método para actualizar la cantidad de pescaditos de un usuario.
    @Override
    public void actualizarPescaditos(int usuarioId, int cantidadSumar) {
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_PESCADITOS)) {

            ps.setInt(1, cantidadSumar);
            ps.setInt(2, usuarioId);

            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error al actualizar pescaditos del usuario {}", usuarioId, e);
            throw new RuntimeException("Error de persistencia al actualizar pescaditos", e);
        }
    }
}