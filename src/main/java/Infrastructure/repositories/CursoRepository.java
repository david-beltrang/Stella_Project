package Infrastructure.repositories;


import Domain.models.Curso;
import Domain.models.CursoValueObjects.NivelCurso;
import Domain.models.CursoValueObjects.Titulo;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CursoRepository implements InterfazCursoRepository {

    private IConexionBD connMgr;

    public CursoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }


    // Método para obtener un curso por su id.
    @Override
    public Optional<Curso> buscarPorId(Integer id) {
        String sql = "SELECT * FROM \"curso\" WHERE id = ?";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(obtenerCurso(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }



    @Override
    public List<Curso> encontrarCursosNoCursadosPorUsuarioId(Integer usuarioId) {
        List<Curso> cursosNoCursados = new ArrayList<>();
        String sql = "SELECT id, titulo, descripcion, nivel, categoria, duracion_minutos, numero_secciones " +
                "FROM \"curso\" " +
                "WHERE id NOT IN (SELECT curso_id FROM \"usuario_curso\" WHERE usuario_id = ?)";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cursosNoCursados.add(obtenerCurso(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener cursos no cursados", e);
        }
        return cursosNoCursados;
    }



    // Método auxiliar para mapear un ResultSet a un Usuario.
    private Curso obtenerCurso(ResultSet rs) throws SQLException {
        return Curso.reconstruir(
                rs.getInt("id"),
                new Titulo(rs.getString("titulo")),
                rs.getString("descripcion"),
                new NivelCurso(rs.getString("nivel")),
                rs.getString("categoria"),
                rs.getInt("duracion_minutos"),
                rs.getInt("numero_secciones")
        );
    }

}
