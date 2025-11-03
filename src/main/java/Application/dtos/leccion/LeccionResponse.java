package Application.dtos.leccion;

public record LeccionResponse(
        int id,
        int seccion_id,
        String titulo,
        int numeroOrden,
        String tipoContenido, // PENDIENTE, COMPLETADA
        String url_video,
        String contenido// Indica si se puede acceder o no
) {
}