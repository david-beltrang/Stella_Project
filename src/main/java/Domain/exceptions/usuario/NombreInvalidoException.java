package Domain.exceptions.usuario;

public class NombreInvalidoException extends RuntimeException{
    public NombreInvalidoException(String nombre) {
        super("El nombre " + nombre + " tiene formato inválido (incluya solo letras)");
    }
}
