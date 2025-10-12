package Domain.exceptions.usuario;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String correo){
        super("Ya existe un usuario registrado con el correo: " + correo);
    }

}
