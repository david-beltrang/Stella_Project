package Application.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PerfilDTO(
        String nombre,
        String nickname,
        String email,
        String contrasena, // Masked or plain as per requirement
        List<String> cursosInscritos,
        int cantidadCursosCompletados,
        LocalDateTime fechaCreacion,
        String rutaImagenStella) {
}
