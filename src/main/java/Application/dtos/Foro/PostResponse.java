package Application.dtos.Foro;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        int id,
        int usuarioId,
        String contenido,
        int likes,
        LocalDateTime fechaCreacion,
        String etiqueta,
        List<ComentarioResponse> comentarios
) {}