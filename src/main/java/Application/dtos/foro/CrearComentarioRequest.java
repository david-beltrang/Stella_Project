package Application.dtos.foro;

public record CrearComentarioRequest(int postId, int usuarioId, String contenidoTexto) {}