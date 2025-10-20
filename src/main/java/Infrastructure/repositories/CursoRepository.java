package Infrastructure.repositories;


import Domain.models.Curso;
import Domain.models.CursoValueObjects.NivelCurso;
import Domain.models.CursoValueObjects.Titulo;
import Domain.repositoriesInterfaces.InterfazCursoRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CursoRepository implements InterfazCursoRepository {

    public CursoRepository() {
        // La creación de la tabla se maneja en el script SQL al iniciar H2.
    }

    @Override
    public List<Curso> listarTodos() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM \"curso\"";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                cursos.add(obtenerCurso(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando cursos: " + e.getMessage(), e);
        }
        return cursos;
    }


    // Método para obtener un curso por su id.
    @Override
    public Optional<Curso> buscarPorId(Integer id) {
        String sql = "SELECT * FROM \"curso\" WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
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


    // Método para eliminar un curso.
    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM \"curso\" WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando curso: " + e.getMessage(), e);
        }
    }


    @Override
    public List<Curso> encontrarCursosNoCursadosPorUsuarioId(Integer usuarioId) {
        List<Curso> cursosNoCursados = new ArrayList<>();
        String sql = "SELECT id, titulo, descripcion, nivel, categoria, duracion_minutos " +
                "FROM curso " +
                "WHERE id NOT IN (SELECT curso_id FROM usuario_curso WHERE usuario_id = ?)";
        try (Connection conn = ConexionBD.getConnection();
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
                rs.getInt("duracion_minutos")
        );
    }

}
