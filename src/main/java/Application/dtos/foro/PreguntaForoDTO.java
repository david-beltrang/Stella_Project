package Application.dtos.foro;

public record PreguntaForoDTO(
        int id,
        int usuarioId,
        String nombreUsuario,
        String titulo,
        String contenido,
        String fechaCreacion,
        int cantidadRespuestas) {
}
