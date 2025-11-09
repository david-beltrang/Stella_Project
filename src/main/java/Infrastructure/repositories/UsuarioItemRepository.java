package Infrastructure.repositories;

import Domain.models.UsuarioItem;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}