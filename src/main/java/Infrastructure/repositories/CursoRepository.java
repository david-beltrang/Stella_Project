package Infrastructure.repositories;

import Domain.models.Curso;
import Domain.models.CursoValueObjects.CursoId;
import Domain.models.CursoValueObjects.Titulo;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CursoRepository implements InterfazCursoRepository {
    private final ConexionBD db; // Clase de utilidad para la conexión

    public CursoRepository(ConexionBD db) {
        this.db = db;
    }

    private Curso mapResultSetToCurso(ResultSet rs) throws SQLException {
        return new Curso(
                new CursoId(rs.getInt("id")),
                new Titulo(rs.getString("titulo")),
                rs.getInt("numero_secciones")
        );
    }

    @Override
    public Curso buscarPorId(CursoId cursoId) {
        String sql = "SELECT id, titulo, numero_secciones FROM curso WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId.valor());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCurso(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar curso por ID: " + e.getMessage());
        }
        return null;
    }
}
