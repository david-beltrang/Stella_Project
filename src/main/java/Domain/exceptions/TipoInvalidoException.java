package Domain.exceptions;

public class TipoInvalidoException extends RuntimeException {
    public TipoInvalidoException(String tipo) {
        super("El tipo de usuario" + tipo + "no es valido");
    }
}
