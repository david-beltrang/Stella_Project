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
                // Aún así actualizar la racha por si estudió hoy
                actualizarRachaUsuario(usuarioId);
                return;
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

        // 4. Actualizar la racha del usuario
        actualizarRachaUsuario(usuarioId);
    }

    /**
     * Actualiza la racha del usuario basándose en la fecha de la última lección.
     * - Si no tiene fecha anterior (primera lección): racha = 1
     * - Si la última lección fue hoy: mantiene la racha actual
     * - Si la última lección fue ayer: incrementa la racha
     * - Si pasaron más de 2 días: resetea la racha a 1
     */
    private void actualizarRachaUsuario(int usuarioId) {
        try (Connection conn = connMgr.getConnection()) {
            // Obtener la fecha de la última lección y la racha actual
            String selectSql = """
                    SELECT racha_dias, ultima_leccion_fecha
                    FROM "usuario_stats"
                    WHERE usuario_id = ?
                    """;

            int rachaActual = 0;
            java.sql.Date ultimaLeccionFecha = null;

            try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        rachaActual = rs.getInt("racha_dias");
                        ultimaLeccionFecha = rs.getDate("ultima_leccion_fecha");
                    }
                }
            }

            // Calcular nueva racha
            java.time.LocalDate hoy = java.time.LocalDate.now();
            int nuevaRacha;

            if (ultimaLeccionFecha == null) {
                // Primera lección
                nuevaRacha = 1;
            } else {
                java.time.LocalDate ultimaFecha = ultimaLeccionFecha.toLocalDate();
                long diasDesdeUltimaLeccion = java.time.temporal.ChronoUnit.DAYS.between(ultimaFecha, hoy);

                if (diasDesdeUltimaLeccion == 0) {
                    // Ya estudió hoy, mantener racha
                    nuevaRacha = rachaActual;
                } else if (diasDesdeUltimaLeccion == 1) {
                    // Estudió ayer, incrementar racha
                    nuevaRacha = rachaActual + 1;
                } else {
                    // Pasaron más de 2 días, resetear racha
                    nuevaRacha = 1;
                }
            }

            // Actualizar usuario_stats
            String updateSql = """
                    UPDATE "usuario_stats"
                    SET racha_dias = ?, ultima_leccion_fecha = ?
                    WHERE usuario_id = ?
                    """;

            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, nuevaRacha);
                pstmt.setDate(2, java.sql.Date.valueOf(hoy));
                pstmt.setInt(3, usuarioId);
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            // No lanzar excepción para no interrumpir el flujo principal
            System.err.println("Error al actualizar racha del usuario " + usuarioId + ": " + e.getMessage());
        }
    }

    @Override
    public double obtenerProgresoPorCurso(int usuarioId, int cursoId) {
        String sqlCompletadas = """
                SELECT COUNT(*) AS completadas
                FROM "progreso_leccion" pl
                JOIN "leccion" l ON pl.leccion_id = l.id
                JOIN "seccion" s ON l.seccion_id = s.id
                WHERE pl.usuario_id = ?
                  AND s.curso_id = ?
                  AND pl.estado = 'COMPLETADA'
                """;

        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlCompletadas)) {

            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, cursoId);

            try (ResultSet rs = pstmt.executeQuery()) {
                int completadas = rs.next() ? rs.getInt("completadas") : 0;
                int totalLecciones = contarLeccionesPorCurso(cursoId);
                if (totalLecciones == 0)
                    return 0.0;
                double porcentaje = (double) completadas / totalLecciones * 100;
                return Math.round(porcentaje * 100.0) / 100.0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener progreso por curso", e);
        }
    }

    private int contarLeccionesPorCurso(int cursoId) {
        String sql = """
                SELECT COUNT(*)
                FROM "leccion" l
                JOIN "seccion" s ON l.seccion_id = s.id
                WHERE s.curso_id = ?
                """;

        try (Connection conn = connMgr.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cursoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando lecciones del curso", e);
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
                            Estado.valueOf(rs.getString("estado"))));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar progreso: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}