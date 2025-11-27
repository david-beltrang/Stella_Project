package Application.dtos.Prueba;

public record IntentoResponse(int id,
                              double puntaje,
                              int totalPreguntas,
                              int aciertos,
                              String mensaje) {
}
