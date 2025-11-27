package Domain.exceptions.curso;

// Excepción lanzada cuando el valor del TipoContenido no es uno de los permitidos
public class TipoContenidoInvalidoException extends RuntimeException {
    public TipoContenidoInvalidoException(String mensaje) {
        super("Tipo de Contenido Inválido: " + mensaje);
    }
}
