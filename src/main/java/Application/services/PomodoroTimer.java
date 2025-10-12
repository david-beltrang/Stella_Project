package main.java.Application.services;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

import static com.example.stellaa.StellaController.timeline;

public class PomodoroTimer {

    private static PomodoroTimer instance; // Singleton (una sola instancia)
    private final IntegerProperty secondsLeft = new SimpleIntegerProperty(1500); //
    private boolean running = false;

    private PomodoroTimer() {}

    public static PomodoroTimer getInstance() {
        if (instance == null) {
            instance = new PomodoroTimer();
        }
        return instance;
    }

    public void start() {
        if (running) return;

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int current = secondsLeft.get();
            if (current > 0) {
                secondsLeft.set(current - 1);
            } else {
                stop();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        running = true;
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        running = false;
    }

    public void reset() {
        stop();
        secondsLeft.set(1500);
    }

    public IntegerProperty secondsLeftProperty() {
        return secondsLeft;
    }

    public boolean isRunning() {
        return running;
    }
}
