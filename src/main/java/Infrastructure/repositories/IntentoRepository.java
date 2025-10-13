package Infrastructure.repositories;

import Application.dtos.internal.IntentoInternal;
import Application.dtos.internal.RespuestaInternal;
import Domain.repositoriesInterfaces.InterfazIntentoRepository;
import Infrastructure.persistence.ConexionBD;

import java.sql.*;
import java.time.LocalDateTime;

// Implementación JDBC real del repositorio de Intentos y Respuestas.
public class IntentoRepository implements InterfazIntentoRepository {

    private static final String SQL_INSERT_INTENTO =
            "INSERT INTO intento (usuario_id, prueba_id, puntaje, fecha_intento) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_RESPUESTA =
            "INSERT INTO respuesta (intento_id, pregunta_id, opcion_seleccionada_id) VALUES (?, ?, ?)";

    // Método para obtener todos los intentos de un usuario.
    @Override
    public int guardarIntento(IntentoInternal intento) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_INTENTO, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, intento.usuarioId());
            ps.setInt(2, intento.pruebaId());
            ps.setDouble(3, intento.score());
            // Convierte LocalDateTime a Timestamp
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            
            // Obtener el ID generado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna el ID generado
                }
                throw new SQLException("Fallo al insertar intento, no se obtuvo ID generado.");
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar intento: " + e.getMessage());
            throw new RuntimeException("Error de persistencia al guardar Intento", e);
        }
    }

    // --- Métodos para actualizar la respuesta de un intento ---
    @Override
    public void guardarRespuesta(RespuestaInternal respuesta) {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_RESPUESTA)) {

            ps.setInt(1, respuesta.intentoId());
            ps.setInt(2, respuesta.preguntaId());
            ps.setInt(3, respuesta.opcionId());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar respuesta: " + e.getMessage());
            throw new RuntimeException("Error de persistencia al guardar Respuesta", e);
        }
    }
}