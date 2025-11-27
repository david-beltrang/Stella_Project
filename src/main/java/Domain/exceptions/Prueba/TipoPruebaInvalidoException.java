package Domain.exceptions.Prueba;

public class TipoPruebaInvalidoException extends RuntimeException {
    public TipoPruebaInvalidoException(String tipoPrueba) {
        super("El tipo de pruena " + tipoPrueba + " es inválido");
    }
}
