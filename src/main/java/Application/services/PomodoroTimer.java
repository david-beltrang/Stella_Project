package Application.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

/**
 * Clase que maneja la lógica del temporizador Pomodoro.
 * No depende de la interfaz gráfica (JavaFX Controller).
 */
public class PomodoroTimer {

    private static PomodoroTimer instance;

    private static final int INITIAL_TIME = 1500; // 25 minutos en segundos
    private final IntegerProperty secondsLeft = new SimpleIntegerProperty(INITIAL_TIME);
    private Timeline timeline;
    private boolean isPaused = true;

    private PomodoroTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public static PomodoroTimer getInstance() {
        if (instance == null) {
            instance = new PomodoroTimer();
        }
        return instance;
    }

    /** Inicia o reanuda el temporizador */
    public void start() {
        if (timeline.getStatus() != Timeline.Status.RUNNING) {
            isPaused = false;
            timeline.play();
        }
    }

    /** Pausa el temporizador */
    public void pause() {
        isPaused = true;
    }

    /** Reinicia el temporizador a 25:00 */
    public void reset() {
        timeline.stop();
        secondsLeft.set(INITIAL_TIME);
        isPaused = true;
    }

    /** Disminuye el contador cada segundo */
    private void tick() {
        if (!isPaused && secondsLeft.get() > 0) {
            secondsLeft.set(secondsLeft.get() - 1);
        } else if (secondsLeft.get() == 0) {
            timeline.stop();
        }
    }

    /** Propiedad observable para conectar con el label */
    public IntegerProperty secondsLeftProperty() {
        return secondsLeft;
    }

    public int getSecondsLeft() {
        return secondsLeft.get();
    }

    public void setSecondsLeft(int seconds) {
        secondsLeft.set(seconds);
    }
}
