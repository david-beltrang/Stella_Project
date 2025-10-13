package Domain.exceptions.usuario;

public class CorreoInvalidoException extends RuntimeException {
    public CorreoInvalidoException(String correo) {
        super("El correo " + correo + " es inválido");
    }
}
