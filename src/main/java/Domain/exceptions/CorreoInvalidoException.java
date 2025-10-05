package Domain.exceptions;

public class CorreoInvalidoException extends RuntimeException {
    public CorreoInvalidoException(String correo) {
        super("El correo " + correo + " es inválido");
    }
}
