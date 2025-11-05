package Application.dtos.Prueba;
import java.util.List;

public record PreguntaResponse(int id,
                               String enunciado,
                               List<OpcionResponse> opciones) {
}
