package Infrastructure.repositories;

import Domain.models.Intento;
import Domain.models.Respuesta;
import Domain.repositoriesInterfaces.InterfazIntentoRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.List;

public class IntentoRepository implements InterfazIntentoRepository {
    private final IConexionBD connMgr;

    public IntentoRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Intento guardar(Intento intento) {
        String sql = "INSERT INTO \"intento\" (usuario_id, prueba_id, puntaje) VALUES (?, ?, ?)";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, intento.getUsuarioId());
            pstmt.setInt(2, intento.getPruebaId());
            pstmt.setDouble(3, intento.getPuntaje().valorPuntaje());

            int affected = pstmt.executeUpdate();
            if (affected == 0) throw new RuntimeException("No se pudo insertar el intento");

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    return Intento.reconstruir(
                            idGenerado,
                            intento.getUsuarioId(),
                            intento.getPruebaId(),
                            intento.getPuntaje(),
                            intento.getFechaIntento()
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar intento", e);
        }
        throw new RuntimeException("No se pudo obtener el ID del intento");
    }

    @Override
    public void guardarRespuestas(List<Respuesta> respuestas) {
        String sql = "INSERT INTO \"respuesta\" (intento_id, pregunta_id, opcion_seleccionada_id) VALUES (?, ?, ?)";

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // SIN TRANSACCIÓN → cada INSERT se hace inmediatamente
            for (Respuesta r : respuestas) {
                pstmt.setInt(1, r.getIntentoId());
                pstmt.setInt(2, r.getPreguntaId());
                pstmt.setInt(3, r.getOpcionSeleccionadaId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar respuestas", e);
        }
    }
}