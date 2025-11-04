package Application.dtos.Prueba;
import java.util.List;

/*Este DTO será el que podrá mandar desde la base de datos la
estructura de la prueba/quiz
*/
public record PruebaResponse(int id,
                             String titulo,
                             String tipo,
                             List<PreguntaResponse> preguntas) {
}
