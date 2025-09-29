package Domain.exceptions;

public class UsernameInvalidoException extends RuntimeException{
    public UsernameInvalidoException(String username){
        super("El username " + username + "es inválido");
    }
}
