package Infrastructure.repositories;

import Domain.models.Ejercicio;
import Domain.repositoriesInterfaces.InterfazEjercicioRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de ejercicios usando H2
 */
public class EjercicioRepository implements InterfazEjercicioRepository {

    private final IConexionBD connMgr;

    public EjercicioRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Ejercicio guardar(Ejercicio ejercicio) {
        String sql = "INSERT INTO \"ejercicio\" (leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ejercicio.getLeccionId());
            stmt.setString(2, ejercicio.getTitulo());
            stmt.setString(3, ejercicio.getInstrucciones());
            stmt.setString(4, ejercicio.getCodigoPlantilla());
            stmt.setString(5, ejercicio.getSolucionEsperada());
            stmt.setInt(6, ejercicio.getPuntos());
            stmt.setInt(7, ejercicio.getNumeroOrden());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return Ejercicio.reconstruir(
                            generatedKeys.getInt(1),
                            ejercicio.getLeccionId(),
                            ejercicio.getTitulo(),
                            ejercicio.getInstrucciones(),
                            ejercicio.getCodigoPlantilla(),
                            ejercicio.getSolucionEsperada(),
                            ejercicio.getPuntos(),
                            ejercicio.getNumeroOrden());
                }
                throw new SQLException("No se generó ID para el ejercicio");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar ejercicio", e);
        }
    }

    @Override
    public Optional<Ejercicio> obtenerPorId(int id) {
        String sql = "SELECT id, leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden FROM \"ejercicio\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEjercicio(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener ejercicio", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Ejercicio> listarPorLeccion(int leccionId) {
        String sql = "SELECT id, leccion_id, titulo, instrucciones, codigo_plantilla, solucion_esperada, puntos, numero_orden FROM \"ejercicio\" WHERE leccion_id = ? ORDER BY numero_orden ASC";
        List<Ejercicio> ejercicios = new ArrayList<>();

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, leccionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ejercicios.add(mapearEjercicio(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ejercicios", e);
        }
        return ejercicios;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM \"ejercicio\" WHERE id = ?";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar ejercicio", e);
        }
    }

    private Ejercicio mapearEjercicio(ResultSet rs) throws SQLException {
        return Ejercicio.reconstruir(
                rs.getInt("id"),
                rs.getInt("leccion_id"),
                rs.getString("titulo"),
                rs.getString("instrucciones"),
                rs.getString("codigo_plantilla"),
                rs.getString("solucion_esperada"),
                rs.getInt("puntos"),
                rs.getInt("numero_orden"));
    }
}
