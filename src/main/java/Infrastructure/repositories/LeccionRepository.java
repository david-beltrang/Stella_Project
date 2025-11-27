package Infrastructure.repositories;

import Domain.models.Leccion;
import Domain.models.LeccionValueObjects.TipoContenido;
import Domain.models.CursoValueObjects.Titulo;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Infrastructure.persistence.IConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class LeccionRepository implements InterfazLeccionRepository {

    private final IConexionBD connMgr;

    public LeccionRepository(IConexionBD connMgr) {
        this.connMgr = connMgr;
    }

    @Override
    public Optional<Leccion> findByCursoIdAndOrdenes(int cursoId, int numeroOrdenSeccion, int numeroOrdenLeccion) {
        String sql = """
                SELECT l.id, l.seccion_id, l.titulo, l.numero_orden, l.tipo_contenido, l.url_video, l.contenido, l.contenido_html, l.pdf_url
                FROM "leccion" l
                INNER JOIN "seccion" s ON l.seccion_id = s.id
                WHERE s.curso_id = ?
                  AND s.numero_orden = ?
                  AND l.numero_orden = ?
                """;

        try (Connection conn = connMgr.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cursoId);
            pstmt.setInt(2, numeroOrdenSeccion);
            pstmt.setInt(3, numeroOrdenLeccion);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Leccion leccion = Leccion.reconstruir(
                            rs.getInt("id"),
                            rs.getInt("seccion_id"),
                            new Titulo(rs.getString("titulo")),
                            rs.getInt("numero_orden"),
                            TipoContenido.valueOf(rs.getString("tipo_contenido")),
                            rs.getString("url_video"),
                            rs.getString("contenido"),
                            rs.getString("contenido_html"),
                            rs.getString("pdf_url"));
                    return Optional.of(leccion);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al buscar lección por cursoId=" + cursoId +
                            ", ordenSeccion=" + numeroOrdenSeccion +
                            ", ordenLeccion=" + numeroOrdenLeccion,
                    e);
        }
        return Optional.empty();
    }
}