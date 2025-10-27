package Infrastructure.repositories;

import Application.dtos.internal.OpcionDetalleInternal;
import Application.dtos.internal.PreguntaConOpcionesInternal;
import Domain.models.Prueba;
import Domain.repositoriesInterfaces.InterfazPruebaRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.IConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementación JDBC real del repositorio de Pruebas.
public class PruebaRepository implements InterfazPruebaRepository {

    private static final String SQL_SELECT_PRUEBA_BY_LECCION =
            "SELECT p.id, p.puntos_maximos, p.categoria, p.tipo FROM prueba p JOIN leccion l ON p.id = l.prueba_id WHERE l.id = ?";
    private static final String SQL_SELECT_PREGUNTAS_AND_OPCIONES =
            "SELECT pr.id AS pregunta_id, pr.enunciado, o.id AS opcion_id, o.texto, o.es_correcta FROM pregunta pr JOIN opcion o ON pr.id = o.pregunta_id WHERE pr.prueba_id = ?";
    private static final String SQL_SELECT_PREGUNTA_BY_ID =
            "SELECT pr.id AS pregunta_id, pr.enunciado, o.id AS opcion_id, o.texto, o.es_correcta FROM pregunta pr JOIN opcion o ON pr.id = o.pregunta_id WHERE pr.id = ?";

    private IConexionBD connMgr;

    public PruebaRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }
    // Método para obtener la prueba de una lección.
    @Override
    public Optional<Prueba> buscarPruebaPorLeccion(int leccionId) {
        Prueba prueba = null;
        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_PRUEBA_BY_LECCION)) {

            ps.setInt(1, leccionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prueba = Prueba.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("puntos_maximos"),
                            rs.getString("categoria"),
                            rs.getString("tipo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar prueba por lección: " + e.getMessage());
        }
        return Optional.ofNullable(prueba);
    }

    // Método para obtener las preguntas de una prueba.
    @Override
    public List<PreguntaConOpcionesInternal> obtenerPreguntas(int pruebaId) {
        return obtenerPreguntasDesdeSQL(SQL_SELECT_PREGUNTAS_AND_OPCIONES, pruebaId);
    }

    // Método para obtener las preguntas de una pregunta.
    @Override
    public List<PreguntaConOpcionesInternal> obtenerPreguntasPorId(int preguntaId) {
        return obtenerPreguntasDesdeSQL(SQL_SELECT_PREGUNTA_BY_ID, preguntaId);
    }

    // --- Método auxiliar para mapear preguntas y opciones ---
    private List<PreguntaConOpcionesInternal> obtenerPreguntasDesdeSQL(String sql, int id) {
        // Un HashMap para agrupar las opciones por ID de Pregunta
        java.util.Map<Integer, PreguntaConOpcionesInternal> preguntasMap = new java.util.LinkedHashMap<>();

        try (Connection conn = connMgr.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int preguntaId = rs.getInt("pregunta_id");

                    if (!preguntasMap.containsKey(preguntaId)) {
                        // Crear nueva Pregunta DTO
                        PreguntaConOpcionesInternal nuevaPregunta = new PreguntaConOpcionesInternal(
                                preguntaId,
                                rs.getString("enunciado"),
                                new ArrayList<>() // Lista de opciones vacía
                        );
                        preguntasMap.put(preguntaId, nuevaPregunta);
                    }

                    // Añadir la opción al DTO de la pregunta
                    PreguntaConOpcionesInternal pregunta = preguntasMap.get(preguntaId);
                    ((ArrayList<OpcionDetalleInternal>) pregunta.opciones()).add(new OpcionDetalleInternal(
                            rs.getInt("opcion_id"),
                            rs.getString("texto"),
                            rs.getBoolean("es_correcta")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener preguntas/opciones: " + e.getMessage());
        }

        // Devolver la lista de DTOs mapeados
        return new ArrayList<>(preguntasMap.values());
    }
}