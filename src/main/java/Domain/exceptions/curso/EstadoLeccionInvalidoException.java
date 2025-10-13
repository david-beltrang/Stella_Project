package Domain.exceptions.curso;

// Excepción lanzada cuando el valor del EstadoLeccion no es uno de los permitidos
public class EstadoLeccionInvalidoException extends RuntimeException {
    public EstadoLeccionInvalidoException(String mensaje) {
        super("Estado de Lección Inválido: " + mensaje);
    }
}
