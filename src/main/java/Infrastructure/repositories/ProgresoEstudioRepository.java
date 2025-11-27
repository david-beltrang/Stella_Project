package Infrastructure.repositories;

import Domain.models.ProgresoEstudio;
import Domain.repositoriesInterfaces.InterfazProgresoEstudioRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProgresoEstudioRepository implements InterfazProgresoEstudioRepository {

    private final IConexionBD connMgr;

    public ProgresoEstudioRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Optional<ProgresoEstudio> obtenerPorUsuarioYFecha(int usuarioId, LocalDate fecha) {
        String sql = """
            SELECT id, usuario_id, fecha, estudio
            FROM "progreso_estudio"
            WHERE usuario_id = ? AND fecha = ?
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProgresoEstudio(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getBoolean("estudio")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener progreso de estudio", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ProgresoEstudio> listarPorRango(int usuarioId, LocalDate inicio, LocalDate fin) {
        List<ProgresoEstudio> lista = new ArrayList<>();
        String sql = """
            SELECT id, usuario_id, fecha, estudio
            FROM "progreso_estudio"
            WHERE usuario_id = ? AND fecha BETWEEN ? AND ?
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setDate(2, Date.valueOf(inicio));
            ps.setDate(3, Date.valueOf(fin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ProgresoEstudio(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getBoolean("estudio")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al listar progreso de estudio", e);
        }
        return lista;
    }

    @Override
    public void guardar(ProgresoEstudio progreso) {
        String sql = """
            MERGE INTO "progreso_estudio" (usuario_id, fecha, estudio) KEY (usuario_id, fecha)
            VALUES (?, ?, ?)
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progreso.getUsuarioId());
            ps.setDate(2, Date.valueOf(progreso.getFecha()));
            ps.setBoolean(3, progreso.isEstudio());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar progreso de estudio", e);
        }
    }

    @Override
    public void actualizar(ProgresoEstudio progreso) {
        String sql = """
            UPDATE "progreso_estudio"
            SET estudio = ?
            WHERE usuario_id = ? AND fecha = ?
            """;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, progreso.isEstudio());
            ps.setInt(2, progreso.getUsuarioId());
            ps.setDate(3, Date.valueOf(progreso.getFecha()));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar progreso de estudio", e);
        }
    }
}