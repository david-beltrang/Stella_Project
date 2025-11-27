package Application.dtos.foro;

import java.time.LocalDateTime;

public record ComentarioResponse(
        int id,
        int usuarioId,
        String contenidoTexto,
        LocalDateTime fecha,
        int likes
) {}