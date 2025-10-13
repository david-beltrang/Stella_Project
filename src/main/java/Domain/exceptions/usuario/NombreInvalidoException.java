package Domain.exceptions.usuario;

// Excepción lanzada cuando el nombre no es válido
public class NombreInvalidoException extends RuntimeException{
    public NombreInvalidoException(String nombre) {
        super("El nombre " + nombre + " tiene formato inválido (incluya solo letras)");
    }
}
