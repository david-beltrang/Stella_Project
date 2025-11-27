package Infrastructure.repositories;

import Application.dtos.leccion.LeccionResponse;
import Domain.models.CursoValueObjects.Titulo;
import Domain.models.Leccion;
import Domain.models.LeccionValueObjects.TipoContenido;
import Application.dtos.seccion.SeccionResponse;
import Domain.repositoriesInterfaces.InterfazSeccionRepository;
import Infrastructure.persistence.IConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeccionRepository implements InterfazSeccionRepository {
    private IConexionBD connMgr;

    public SeccionRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public List<SeccionResponse> encontrarSeccionesConLecciones(int cursoId) {
        List<SeccionResponse> secciones = new ArrayList<>();
        String sql = "SELECT s.id, s.titulo, s.numero_orden, l.id AS leccion_id, l.seccion_id, l.titulo AS leccion_titulo, "
                +
                "l.numero_orden AS leccion_numero_orden, l.tipo_contenido, l.url_video, l.contenido, l.contenido_html, l.pdf_url "
                +
                "FROM \"seccion\" s " +
                "LEFT JOIN \"leccion\" l ON s.id = l.seccion_id " +
                "WHERE s.curso_id = ? " +
                "ORDER BY s.numero_orden, l.numero_orden";

        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cursoId);

            try (ResultSet rs = pstmt.executeQuery()) {

                int currentSeccionId = -1;
                String currentSeccionTitulo = null;
                int currentNumeroOrden = 0;
                List<LeccionResponse> lecciones = new ArrayList<>();

                while (rs.next()) {
                    int seccionId = rs.getInt("id");

                    // Si cambiamos de sección, guardamos la anterior
                    if (seccionId != currentSeccionId) {
                        if (currentSeccionId != -1) {
                            secciones.add(new SeccionResponse(
                                    currentSeccionId,
                                    currentSeccionTitulo,
                                    currentNumeroOrden,
                                    new ArrayList<>(lecciones)));
                            lecciones.clear();
                        }

                        // Actualizamos datos de la nueva sección
                        currentSeccionId = seccionId;
                        currentSeccionTitulo = rs.getString("titulo");
                        currentNumeroOrden = rs.getInt("numero_orden");
                    }

                    // Procesar lección si existe
                    int leccionId = rs.getInt("leccion_id");
                    if (!rs.wasNull()) {
                        Leccion leccion = Leccion.reconstruir(
                                leccionId,
                                rs.getInt("seccion_id"),
                                new Titulo(rs.getString("leccion_titulo")),
                                rs.getInt("leccion_numero_orden"),
                                TipoContenido.valueOf(rs.getString("tipo_contenido").toUpperCase()),
                                rs.getString("url_video"),
                                rs.getString("contenido"),
                                rs.getString("contenido_html"),
                                rs.getString("pdf_url"));

                        lecciones.add(new LeccionResponse(
                                leccion.getId(),
                                leccion.getSeccion_id(),
                                leccion.getTitulo().valorTitulo(),
                                leccion.getNumeroOrden(),
                                leccion.getTipoContenido().name(),
                                leccion.getUrl_video(),
                                leccion.getContenido(),
                                leccion.getContenidoHtml(),
                                leccion.getPdfUrl()));
                    }
                }

                // Agregar última sección procesada
                if (currentSeccionId != -1) {
                    secciones.add(new SeccionResponse(
                            currentSeccionId,
                            currentSeccionTitulo,
                            currentNumeroOrden,
                            new ArrayList<>(lecciones)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener secciones y lecciones", e);
        }

        return secciones;
    }

}