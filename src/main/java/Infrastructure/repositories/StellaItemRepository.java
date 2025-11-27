package Infrastructure.repositories;

import Domain.models.StellaItem;
import Domain.repositoriesInterfaces.InterfazStellaItemRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class StellaItemRepository implements InterfazStellaItemRepository {

    private final IConexionBD connMgr;

    public StellaItemRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Optional<StellaItem> findByItemId(int itemId) {
        String sql = """
                        SELECT id, item_id, image_path
                        FROM \"stella_item\"
                        WHERE item_id = ?
                        """;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(StellaItem.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("item_id"),
                            rs.getString("image_path")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar stella_item por item ID", e);
        }
        return Optional.empty();
    }
}