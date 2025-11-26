package Infrastructure.repositories;

import Domain.models.LeccionValueObjects.Estado;
import Domain.models.ProgresoLeccion;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class ProgresoRepository implements InterfazProgresoRepository {

    private final IConexionBD connMgr;

    public ProgresoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void marcarCompletada(int usuarioId, int leccionId) {
        // 1. Verificar si ya existe
        Optional<ProgresoLeccion> existente = findByUsuarioIdAndLeccionId(usuarioId, leccionId);

        if (existente.isPresent()) {
            // 2. Ya existe → ACTUALIZAR
            if (existente.get().getEstado() == Estado.COMPLETADA) {
                return; // Ya está completada → no hacer nada
            }

            String updateSql = """
            UPDATE "progreso_leccion"
            SET estado = 'COMPLETADA'
            WHERE usuario_id = ? AND leccion_id = ?
            """;

            try (Connection conn = connMgr.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, usuarioId);
                pstmt.setInt(2, leccionId);
                pstmt.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar progreso", e);
            }
        } else {
            // 3. No existe → INSERTAR
            String insertSql = """
            INSERT INTO "progreso_leccion" (usuario_id, leccion_id, estado)
            VALUES (?, ?, 'COMPLETADA')
            """;

            try (Connection conn = connMgr.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, usuarioId);
                pstmt.setInt(2, leccionId);
                pstmt.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error al insertar progreso", e);
            }
        }
    }

    @Override
    public double obtenerProgresoPorCurso(int usuarioId, int cursoId) {
        String sql = """
        SELECT COUNT(*) AS completadas
        FROM "progreso_leccion" pl
        JOIN "leccion" l ON pl.leccion_id = l.id
        JOIN "seccion" s ON l.seccion_id = s.id
        WHERE pl.usuario_id = ?
          AND s.curso_id = ?
          AND pl.estado = 'COMPLETADA';
        """;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, cursoId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int completadas = rs.getInt("completadas");
                    // Conteo real de lecciones por curso
                    int totalLecciones = contarLeccionesPorCurso(cursoId, conn);
                    if (totalLecciones == 0) return 0.0;

                    // REDONDEO A 2 DECIMALES - PROFESIONAL
                    double porcentaje = (completadas / (double) totalLecciones) * 100;
                    return Math.round(porcentaje * 100.0) / 100.0;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener progreso por curso", e);
        }
        return 0.0;
    }

    private int contarLeccionesPorCurso(int cursoId, Connection conn) throws Exception {
        String sql = """
        SELECT COUNT(*) 
        FROM "leccion" l 
        JOIN "seccion" s ON l.seccion_id = s.id 
        WHERE s.curso_id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public Optional<ProgresoLeccion> findByUsuarioIdAndLeccionId(int usuarioId, int leccionId) {
        String sql = """
        
                SELECT id, usuario_id, leccion_id, estado
        FROM "progreso_leccion"
        WHERE usuario_id = ? AND leccion_id = ?
        """;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, leccionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ProgresoLeccion.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getInt("leccion_id"),
                            Estado.valueOf(rs.getString("estado"))
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar progreso: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}