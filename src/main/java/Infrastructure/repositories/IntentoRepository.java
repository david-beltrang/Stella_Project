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
        String sql = "INSERT INTO intento (usuario_id, prueba_id, puntaje) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, intento.getUsuarioId());
            pstmt.setInt(2, intento.getPruebaId());
            pstmt.setDouble(3, intento.getPuntaje().valorPuntaje());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Intento.reconstruir(
                            rs.getInt("id"),
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
        String sql = "INSERT INTO respuesta (intento_id, pregunta_id, opcion_seleccionada_id) VALUES (?, ?, ?)";

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

