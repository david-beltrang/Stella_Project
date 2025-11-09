package Application.dtos.Foro;

public record CrearComentarioRequest(int postId, int usuarioId, String contenidoTexto) {}