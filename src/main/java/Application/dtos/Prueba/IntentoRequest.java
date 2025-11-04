package Application.dtos.Prueba;

import java.util.List;

public record IntentoRequest(int usuarioId,
                             int pruebaId,
                             List<RespuestaRequest> respuestas) {
}
