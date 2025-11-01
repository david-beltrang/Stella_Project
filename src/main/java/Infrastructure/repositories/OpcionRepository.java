package Infrastructure.repositories;

import Domain.repositoriesInterfaces.InterfazOpcionRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class OpcionRepository implements InterfazOpcionRepository {
    private final IConexionBD connMgr;

    public OpcionRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Map<Integer, Integer> encontrarOpcionesCorrectasPorPruebaId(int pruebaId) {
        Map<Integer, Integer> correctas = new HashMap<>();
        String sql =
            "SELECT pr.id AS pregunta_id, o.id AS opcion_id " +
            "FROM \"pregunta\" pr " +
            "JOIN \"opcion\" o ON o.pregunta_id = pr.id " +
            "WHERE pr.prueba_id = ? AND o.es_correcta = true";

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, pruebaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    correctas.put(rs.getInt("pregunta_id"), rs.getInt("opcion_id"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener opciones correctas", e);
        }
        return correctas;
    }
}