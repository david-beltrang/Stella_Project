package Infrastructure.repositories;

import Application.dtos.Prueba.*;
import Domain.repositoriesInterfaces.InterfazPruebaRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PruebaRepository implements InterfazPruebaRepository {
    private final IConexionBD connMgr;

    public PruebaRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Optional<PruebaResponse> encontrarPorSeccionId(Integer seccionId) {
        String sql =
            "SELECT p.id AS prueba_id, p.titulo AS prueba_titulo, p.tipo, " +
                   "pr.id AS pregunta_id, pr.enunciado, " +
                   "o.id AS opcion_id, o.texto " +
            "FROM \"prueba\" p" +
            "JOIN \"pregunta\" pr ON pr.prueba_id = p.id " +
            "JOIN \"opcion\" o ON o.pregunta_id = pr.id " +
            "WHERE p.seccion_id = ? " +
            "ORDER BY pr.id, o.id";

        try (Connection conn = connMgr.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, seccionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                int currentPreguntaId = -1;
                List<OpcionResponse> opciones = new ArrayList<>();
                List<PreguntaResponse> preguntas = new ArrayList<>();

                int pruebaId = rs.getInt("prueba_id");
                String pruebaTitulo = rs.getString("prueba_titulo");
                String tipo = rs.getString("tipo");

                do {
                    int preguntaId = rs.getInt("pregunta_id");
                    if (preguntaId != currentPreguntaId) {
                        if (!opciones.isEmpty()) {
                            preguntas.add(new PreguntaResponse(currentPreguntaId, rs.getString("enunciado"), new ArrayList<>(opciones)));
                            opciones.clear();
                        }
                        currentPreguntaId = preguntaId;
                    }
                    opciones.add(new OpcionResponse(rs.getInt("opcion_id"), rs.getString("texto")));
                } while (rs.next());

                if (!opciones.isEmpty()) {
                    preguntas.add(new PreguntaResponse(currentPreguntaId, rs.getString("enunciado"), new ArrayList<>(opciones)));
                }

                return Optional.of(new PruebaResponse(pruebaId, pruebaTitulo, tipo, preguntas));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar prueba", e);
        }
    }
}
