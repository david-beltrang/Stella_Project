package Application.dtos.leccion;

public record LeccionResponse(
                int id,
                int seccion_id,
                String titulo,
                int numeroOrden,
                String tipoContenido,
                String url_video,
                String contenido,
                String contenidoHtml,
                String pdfUrl) {
}