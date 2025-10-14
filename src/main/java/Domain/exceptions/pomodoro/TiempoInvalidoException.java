package Domain.exceptions.pomodoro;

public class TiempoInvalidoException extends RuntimeException {
    public TiempoInvalidoException(String message) {
        super(message);
    }
}
