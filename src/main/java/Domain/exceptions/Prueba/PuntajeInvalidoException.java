package Domain.exceptions.Prueba;

public class PuntajeInvalidoException extends RuntimeException {
    public PuntajeInvalidoException(double puntaje) {
        super("El puntaje " + puntaje + " es inválido");
    }
}
