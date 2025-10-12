package Domain.exceptions.usuario;

public class UsernameInvalidoException extends RuntimeException{
    public UsernameInvalidoException(String username){
        super("El username " + username + "es inválido");
    }
}
