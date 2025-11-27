package Infrastructure.repositories;

import Domain.models.Item;
import Domain.repositoriesInterfaces.InterfazItemRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemRepository implements InterfazItemRepository {

    private final IConexionBD connMgr;

    public ItemRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Optional<Item> findById(int id) {
        String sql = "SELECT id, nombre, descripcion, precio, image_path FROM \"item\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(Item.reconstruir(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getInt("precio"),
                            rs.getString("image_path")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar item por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, precio, image_path FROM \"item\"";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(Item.reconstruir(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("precio"),
                        rs.getString("image_path")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al listar items", e);
        }
        return items;
    }
}