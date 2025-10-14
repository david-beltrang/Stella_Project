package Domain.models.PomodoroValueObjects;
import Domain.exceptions.pomodoro.TiempoInvalidoException;

//Se manejará proximamente la lógica de negocio en las excepciones de dominio
public record TiempoEstudio(int minutos) {
    //se llama la funcion esTiempoValido
    public TiempoEstudio {
        if (!esTiempoValido(minutos)) {
            throw new TiempoInvalidoException("Tiempo de estudio debe ser 25, 30, 45 o 60 minutos.");
        }
    }

    //Se valida que que el tiempo de estudio este entre los tiempos definidos en las regas de negocio
    private static boolean esTiempoValido(int minutos) {
        int[] tiemposValidos = {25, 30, 45, 60};
        for (int t : tiemposValidos) if (t == minutos) return true;
        return false;
    }
}
