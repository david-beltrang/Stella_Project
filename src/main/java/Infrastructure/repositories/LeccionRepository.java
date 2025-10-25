package Infrastructure.repositories;

import Domain.models.CursoValueObjects.Titulo;
import Domain.models.Leccion;
import Domain.models.LeccionValueObjects.TipoContenido;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC real del repositorio de Lecciones.
public class LeccionRepository implements InterfazLeccionRepository {

    private final ConexionBD db; // Clase de utilidad para la conexión

    public LeccionRepository(ConexionBD db) {
        this.db = db;
    }

    private Leccion mapResultSetToLeccion(ResultSet rs) throws SQLException {
        return new Leccion(
                rs.getInt("id"),

                // Verificcar si es NULL
                rs.wasNull() ? null : rs.getInt("curso_id"),

                // VO: Titulo
                new Titulo(rs.getString("titulo")),

                // Primitivos:
                rs.getInt("numero_seccion"),
                rs.getInt("numero_orden"),

                // Enum: TipoContenido
                TipoContenido.valueOf(rs.getString("tipo_contenido")),

                // String:
                rs.getString("contenido_html")
        );
    }

    @Override
    public Leccion buscarPorId(Integer leccionId) {
        String sql = "SELECT * FROM leccion WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, leccionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLeccion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lección por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<Leccion> buscarPorCursoId(Integer cursoId) {
        String sql = "SELECT * FROM leccion WHERE curso_id = ? ORDER BY numero_seccion, numero_orden";
        List<Leccion> lecciones = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lecciones.add(mapResultSetToLeccion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lecciones por Curso ID: " + e.getMessage());
        }
        return lecciones;
    }

    /**
     * Lógica SQL para encontrar la lección anterior en la secuencia.
     */
    @Override
    public Leccion buscarLeccionAnterior(Integer cursoId, int seccionActual, int ordenActual) {
        String sql = "SELECT * FROM leccion WHERE curso_id = ? AND " +
                // Opción 1: Mismo número de sección, pero orden menor (lección anterior)
                " (numero_seccion = ? AND numero_orden < ?) " +
                // Opción 2: Sección anterior (con cualquier orden)
                " OR numero_seccion < ? " +
                "ORDER BY numero_seccion DESC, numero_orden DESC LIMIT 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cursoId);
            stmt.setInt(2, seccionActual);
            stmt.setInt(3, ordenActual);
            stmt.setInt(4, seccionActual);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLeccion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lección anterior: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lógica SQL para encontrar la siguiente lección en la secuencia.
     */
    @Override
    public Leccion buscarProximaLeccion(Integer cursoId, int seccionActual, int ordenActual) {
        String sql = "SELECT * FROM leccion WHERE curso_id = ? AND " +
                " (numero_seccion = ? AND numero_orden > ?) " +
                " OR numero_seccion > ? " +
                "ORDER BY numero_seccion ASC, numero_orden ASC LIMIT 1";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Desempaquetado del VO para el JDBC: cursoId.valor()
            stmt.setInt(1, cursoId);
            stmt.setInt(2, seccionActual);
            stmt.setInt(3, ordenActual);
            stmt.setInt(4, seccionActual);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLeccion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar próxima lección: " + e.getMessage());
        }
        return null;
    }
}
