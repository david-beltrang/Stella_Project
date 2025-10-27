package Infrastructure.repositories;

import Application.dtos.internal.ContenidoLeccionInternal;
import Domain.models.Leccion;
import Domain.models.Pregunta;
import Domain.models.Opcion;
import Domain.models.LeccionValueObjects.TipoContenido;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

// Implementación JDBC real del repositorio de Lecciones.
public class LeccionRepository implements InterfazLeccionRepository {

    private static final String SQL_SELECT_CONTENIDO =
            "SELECT id, titulo, tipo_contenido, contenido_html, prueba_id FROM leccion WHERE id = ?";

    private static final String SQL_SELECT_BY_SECTION =
            "SELECT id, curso_id, numero_seccion, numero_orden, titulo, tipo_contenido, contenido_html, prueba_id " +
                    "FROM leccion WHERE curso_id = ? AND numero_seccion = ? ORDER BY numero_orden ASC";

    // Consulta para obtener Preguntas y sus Opciones asociadas a una Lección (vía leccion_id)
    private static final String SQL_SELECT_PREGUNTAS_BY_LECCION =
            "SELECT p.id as pregunta_id, p.enunciado, o.id as opcion_id, o.texto, o.es_correcta " +
                    "FROM pregunta p " +
                    "LEFT JOIN opcion o ON p.id = o.pregunta_id " +
                    "WHERE p.leccion_id = ? ORDER BY p.id, o.id";

    private IConexionBD connMgr;

    public LeccionRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }
    // Método para obtener el contenido de una lección.
    @Override
    public Optional<ContenidoLeccionInternal> obtenerContenido(int id) {
        ContenidoLeccionInternal contenido = null;

        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_CONTENIDO)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    contenido = mapearContenido(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener contenido de lección: " + e.getMessage());
        }
        return Optional.ofNullable(contenido);
    }

    // Método para obtener las lecciones de un curso y sección.
    @Override
    public List<Leccion> buscarPorCursoYSeccion(int cursoId, int numeroSeccion) {
        List<Leccion> lecciones = new ArrayList<>();
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_SECTION)) {

            ps.setInt(1, cursoId);
            ps.setInt(2, numeroSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lecciones.add(mapearLeccion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar lecciones por sección: " + e.getMessage());
        }
        return lecciones;
    }

    // Método para obtener las preguntas asociadas a una lección.
    @Override
    public List<Pregunta> buscarPreguntasAsociadas(int leccionId) {
        Map<Integer, Pregunta> preguntasMap = new HashMap<>();

        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_PREGUNTAS_BY_LECCION)) {

            ps.setInt(1, leccionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Mapeo y agrupación de preguntas/opciones
                    Integer preguntaId = rs.getInt("pregunta_id");
                    String enunciado = rs.getString("enunciado");

                    Pregunta pregunta = preguntasMap.get(preguntaId);
                    if (pregunta == null) {
                        // Creamos una nueva entidad Pregunta (con lista vacía para las opciones)
                        pregunta = new Pregunta(preguntaId, enunciado, new ArrayList<>());
                        preguntasMap.put(preguntaId, pregunta);
                    }

                    // Añadir opción si existe (opcion_id no nulo)
                    int opcionId = rs.getInt("opcion_id");
                    if (!rs.wasNull()) { // Verifica si el ID de la opción NO es nulo
                        Opcion opcion = new Opcion(
                                opcionId,
                                rs.getString("texto"),
                                rs.getBoolean("es_correcta")
                        );
                        pregunta.getOpciones().add(opcion);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar preguntas asociadas: " + e.getMessage());
        }

        return new ArrayList<>(preguntasMap.values());
    }

    // Mapea un ResultSet al DTO de contenido de la lección (usado por SesionEstudioService).
    private ContenidoLeccionInternal mapearContenido(ResultSet rs) throws SQLException {
        int pruebaIdInt = rs.getInt("prueba_id");
        // Verifica si el valor de la BD es NULL y usa Integer Object
        Integer pruebaId = rs.wasNull() ? null : pruebaIdInt;

        return new ContenidoLeccionInternal(
                rs.getInt("id"),
                rs.getString("titulo"),
                TipoContenido.fromString(rs.getString("tipo_contenido")),
                rs.getString("contenido_html"),
                pruebaId
        );
    }

    // Mapea un ResultSet a la entidad Leccion (usado por LeccionService).
    private Leccion mapearLeccion(ResultSet rs) throws SQLException {
        int pruebaIdInt = rs.getInt("prueba_id");
        // Verifica si el valor de la BD es NULL y usa Integer Object para el constructor
        Integer pruebaId = rs.wasNull() ? null : pruebaIdInt;

        return new Leccion(
                rs.getInt("id"),
                rs.getInt("curso_id"),
                rs.getInt("numero_seccion"),
                rs.getInt("numero_orden"),
                rs.getString("titulo"),
                rs.getString("tipo_contenido"), // Se pasa el String, el constructor de Leccion lo valida
                rs.getString("contenido_html"),
                pruebaId
        );
    }
}
