package Domain.exceptions.usuario;

// Excepción lanzada cuando el username no es válido
public class UsernameInvalidoException extends RuntimeException{
    public UsernameInvalidoException(String username){
        super("El username " + username + "es inválido");
    }
}
