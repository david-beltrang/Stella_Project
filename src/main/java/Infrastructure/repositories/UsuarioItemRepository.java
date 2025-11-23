package Infrastructure.repositories;

import Domain.models.UsuarioItem;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioItemRepository implements InterfazUsuarioItemRepository {

    private final IConexionBD connMgr;

    public UsuarioItemRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void comprarItem(int usuarioId, int itemId, int costo) {
        if (tieneItem(usuarioId, itemId)) {
            return;
        }

        String insertSql = "INSERT INTO \"usuario_item\" (usuario_id, item_id, fecha_compra, es_activo) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP, TRUE)";

        String updateSql = "UPDATE \"usuario_stats\" SET pescaditos = pescaditos - ? " +
                "WHERE usuario_id = ? AND pescaditos >= ?";

        try (Connection conn = connMgr.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insertar compra
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, usuarioId);
                psInsert.setInt(2, itemId);
                int rows = psInsert.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return;
                }
            }

            // 2. Restar pescaditos
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, costo);
                psUpdate.setInt(2, usuarioId);
                psUpdate.setInt(3, costo);
                int rows = psUpdate.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    throw new RuntimeException("Pescaditos insuficientes");
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (e.getSQLState().startsWith("23")) {
                return;
            }
            throw new RuntimeException("Error al procesar compra", e);
        }
    }

    @Override
    public boolean tieneItem(int usuarioId, int itemId) {
        String sql = "SELECT 1 FROM \"usuario_item\" WHERE usuario_id = ? AND item_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<UsuarioItem> findByUsuarioId(int usuarioId) {
        List<UsuarioItem> items = new ArrayList<>();
        String sql = "SELECT usuario_id, item_id, fecha_compra, es_activo FROM \"usuario_item\" WHERE usuario_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(UsuarioItem.reconstruir(
                            rs.getInt("usuario_id"),
                            rs.getInt("item_id"),
                            rs.getTimestamp("fecha_compra").toLocalDateTime(),
                            rs.getBoolean("es_activo")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al listar items del usuario", e);
        }
        return items;
    }

    @Override
    public int obtenerPescaditos(int usuarioId) {
        String sql = "SELECT pescaditos FROM \"usuario_stats\" WHERE usuario_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("pescaditos") : 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Optional<UsuarioItem> obtenerItemActivo(int usuarioId) {
        String sql = "SELECT usuario_id, item_id, fecha_compra, es_activo FROM \"usuario_item\" " +
                "WHERE usuario_id = ? AND es_activo = TRUE";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(UsuarioItem.reconstruir(
                            rs.getInt("usuario_id"),
                            rs.getInt("item_id"),
                            rs.getTimestamp("fecha_compra").toLocalDateTime(),
                            rs.getBoolean("es_activo")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener item activo del usuario", e);
        }
        return Optional.empty();
    }

    @Override
    public void cambiarItemActivo(int usuarioId, int nuevoItemId) {
        String desactivarTodos = "UPDATE \"usuario_item\" SET es_activo = FALSE WHERE usuario_id = ?";
        String activarNuevo = "UPDATE \"usuario_item\" SET es_activo = TRUE WHERE usuario_id = ? AND item_id = ?";

        try (Connection conn = connMgr.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Desactivar todos los items del usuario
            try (PreparedStatement psDesactivar = conn.prepareStatement(desactivarTodos)) {
                psDesactivar.setInt(1, usuarioId);
                psDesactivar.executeUpdate();
            }

            // 2. Activar el nuevo item (solo si el usuario lo tiene)
            try (PreparedStatement psActivar = conn.prepareStatement(activarNuevo)) {
                psActivar.setInt(1, usuarioId);
                psActivar.setInt(2, nuevoItemId);
                int rows = psActivar.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    throw new RuntimeException("El usuario no tiene el item con id: " + nuevoItemId);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar item activo", e);
        }
    }
}