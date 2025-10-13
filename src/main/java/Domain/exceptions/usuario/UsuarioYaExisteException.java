package Domain.exceptions.usuario;

// Excepción lanzada cuando se intenta registrar un usuario que ya existe
public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String correo){
        super("Ya existe un usuario registrado con el correo: " + correo);
    }

}
