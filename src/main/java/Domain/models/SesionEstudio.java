package Domain.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que registra los tiempos de una sesión de estudio.
 * No es un Aggregate Root, pero es una entidad importante para las estadísticas.
 */
public class SesionEstudio {

    private Integer id;
    private final int usuarioId;
    private int tiempoEstudioMinutos;
    private int tiempoDescansoMinutos;
    private final LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // ------------------ CONSTRUCTOR PRIVADO ----------------------

    private SesionEstudio(Integer id, int usuarioId, int tiempoEstudioMinutos, int tiempoDescansoMinutos, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tiempoEstudioMinutos = tiempoEstudioMinutos;
        this.tiempoDescansoMinutos = tiempoDescansoMinutos;
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula.");
        this.fechaFin = fechaFin;
    }

    // ------------------ FACTORY METHODS ------------------

    // Inicia una nueva sesión de estudio.
    public static SesionEstudio iniciar(int usuarioId) {
        // Inicia con tiempo y descanso en 0.
        return new SesionEstudio(null, usuarioId, 0, 0, LocalDateTime.now(), null);
    }

    // Reconstruye la entidad desde la base de datos.
    public static SesionEstudio reconstruir(Integer id, int usuarioId, int tiempoEstudio, int tiempoDescanso, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return new SesionEstudio(id, usuarioId, tiempoEstudio, tiempoDescanso, fechaInicio, fechaFin);
    }

    // ---------- COMPORTAMIENTOS y LÓGICA DEL DOMINIO ----------

    // Finaliza la sesión y calcula el tiempo total (en minutos).
    public void finalizar(int tiempoEstudioMinutos, int tiempoDescansoMinutos) {
        if (this.fechaFin != null) {
            throw new IllegalStateException("La sesión ya ha sido finalizada.");
        }
        if (tiempoEstudioMinutos < 0 || tiempoDescansoMinutos < 0) {
            throw new IllegalArgumentException("Los tiempos no pueden ser negativos.");
        }

        this.tiempoEstudioMinutos = tiempoEstudioMinutos;
        this.tiempoDescansoMinutos = tiempoDescansoMinutos;
        this.fechaFin = LocalDateTime.now();
    }

    // ---------- GETTERS ---------
    public Integer getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getTiempoEstudioMinutos() { return tiempoEstudioMinutos; }
    public int getTiempoDescansoMinutos() { return tiempoDescansoMinutos; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaFin() { return fechaFin; }
}