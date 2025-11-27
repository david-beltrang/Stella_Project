package Infrastructure.repositories;

import Domain.models.UsuarioCurso;
import Domain.repositoriesInterfaces.InterfazUsuarioCursoRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioCursoRepository implements InterfazUsuarioCursoRepository {

    private IConexionBD connMgr;

    public UsuarioCursoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public void inscribir(UsuarioCurso usuarioCurso) {
        // Validar que el curso exista ANTES de intentar insertar
        String checkSql = "SELECT 1 FROM \"curso\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, usuarioCurso.getCursoId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("El curso con id " + usuarioCurso.getCursoId() + " no existe");
                }
            }

            // Si existe, ahora sí insertamos
            String insertSql = "INSERT INTO \"usuario_curso\" (usuario_id, curso_id, fecha) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, usuarioCurso.getUsuarioId());
                insertStmt.setInt(2, usuarioCurso.getCursoId());
                insertStmt.setTimestamp(3, java.sql.Timestamp.valueOf(usuarioCurso.getFecha()));
                insertStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar inscripción", e);
        }
    }

    @Override
    public boolean existeInscripcion(Integer usuarioId, Integer cursoId) {
        String sql = "SELECT COUNT(*) FROM \"usuario_curso\" WHERE usuario_id = ? AND curso_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, cursoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar inscripción", e);
        }
        return false;
    }

    @Override
    public List<UsuarioCurso> encontrarPorUsuarioId(Integer usuario_id) {
        List<UsuarioCurso> inscripciones = new ArrayList<>();
        String sql = "SELECT usuario_id, curso_id, fecha FROM \"usuario_curso\" WHERE usuario_id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuario_id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inscripciones.add(obtenerUsuarioCurso(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener inscripciones por usuario", e);
        }
        return inscripciones;
    }

    // Método auxiliar para mapear un ResultSet a un UsuarioCurso.
    private UsuarioCurso obtenerUsuarioCurso(ResultSet rs) throws SQLException {
        return UsuarioCurso.reconstruir(
                rs.getInt("usuario_id"),
                rs.getInt("curso_id"),
                rs.getTimestamp("fecha").toLocalDateTime()
        );
    }

    public void inicializarProgresoTodasLecciones(int usuarioId, int cursoId) {
        String sql = """
        INSERT INTO "progreso_leccion" (usuario_id, leccion_id, estado)
        SELECT ?, l.id, 'EN_PROGRESO'
        FROM "leccion" l
        JOIN "seccion" s ON l.seccion_id = s.id
        WHERE s.curso_id = ?
          AND NOT EXISTS (
              SELECT 1 FROM "progreso_leccion" pl 
              WHERE pl.usuario_id = ? AND pl.leccion_id = l.id
          )
        """;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. Enlazar el primer marcador '?' (usuario_id)
            pstmt.setInt(1, usuarioId);

            // 2. Enlazar el segundo marcador '?' (curso_id para la cláusula WHERE)
            pstmt.setInt(2, cursoId);

            pstmt.setInt(3, usuarioId);

            // Ejecutar la actualización (es un INSERT, por lo que usamos executeUpdate)
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar el progreso de las lecciones para el usuario: " + usuarioId + " en curso: " + cursoId, e);
        }
    }


}