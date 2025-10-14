package Domain.models.PomodoroValueObjects;
import Domain.exceptions.pomodoro.TiempoInvalidoException;

public record TiempoDescanso(int minutos) {
    public TiempoDescanso {
        if (!esTiempoValido(minutos)) {
            throw new TiempoInvalidoException("Tiempo de descanso debe ser 5, 10, 15 o 30 minutos.");
        }
    }

    private static boolean esTiempoValido(int minutos) {
        int[] tiemposValidos = {5, 10, 15, 30};
        for (int t : tiemposValidos) if (t == minutos) return true;
        return false;
    }
}