package Domain.models;

import Domain.models.PomodoroValueObjects.TiempoDescanso;
import Domain.models.PomodoroValueObjects.TiempoEstudio;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Entidad SesionEstudio
 * Contiene Value Objects para manejr excepciones y reglas de negocio (TiempoEstudio, TiempoDescanso).
 * Factory methods: crearNueva para crear desde la UI y para poder reconstruir la sesión desde la BD.
 */
public class SesionEstudio {
    private Integer id; // null si no se ha guardado en la BD
    private int usuarioId;
    private TiempoEstudio tiempoEstudio;
    private TiempoDescanso tiempoDescanso;
    private Timestamp fechaInicio; //Se utiliza timestamp para poder manejar las fechas en la base de datos
    private Timestamp fechaFinal; //Se utiliza timestamp para poder manejar las fechas en la base de datos

    // Constructor privado para garantizar integridad
    private SesionEstudio(Integer id, int usuarioId, TiempoEstudio tiempoEstudio, TiempoDescanso tiempoDescanso,
                          Timestamp fechaInicio, Timestamp fechaFinal) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tiempoEstudio = Objects.requireNonNull(tiempoEstudio, "tiempoEstudio no puede ser nulo");
        this.tiempoDescanso = Objects.requireNonNull(tiempoDescanso, "tiempoDescanso no puede ser nulo");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "fechaInicio no puede ser nulo");
        this.fechaFinal = fechaFinal; // Puede ser null si no ha terminado la sesión
    }

    // Factory method para crear una nueva sesión desde la UI
    public static SesionEstudio crearNueva(int usuarioId, int tiempoEstudioMinutos, int tiempoDescansoMinutos) {
        TiempoEstudio tiempoEstudio = new TiempoEstudio(tiempoEstudioMinutos);
        TiempoDescanso tiempoDescanso = new TiempoDescanso(tiempoDescansoMinutos);
        return new SesionEstudio(null, usuarioId, tiempoEstudio, tiempoDescanso, new Timestamp(System.currentTimeMillis()), null);
    }

    // Factory method para reconstruir desde la BD
    public static SesionEstudio reconstruir(Integer id, int usuarioId, TiempoEstudio tiempoEstudio,
                                            TiempoDescanso tiempoDescanso, Timestamp fechaInicio, Timestamp fechaFinal) {
        return new SesionEstudio(id, usuarioId, tiempoEstudio, tiempoDescanso, fechaInicio, fechaFinal);
    }

    // Finalizar la sesión para poder actualizar o insertar una nueva sesión de estudio en la base de datos
    public void finalizar() {
        if (this.fechaFinal != null) {
            throw new IllegalStateException("La sesión ya ha sido finalizada.");
        }
        this.fechaFinal = new Timestamp(System.currentTimeMillis());
    }

    // Getters
    public Integer getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public TiempoEstudio getTiempoEstudio() { return tiempoEstudio; }
    public TiempoDescanso getTiempoDescanso() { return tiempoDescanso; }
    public Timestamp getFechaInicio() { return fechaInicio; }
    public Timestamp getFechaFinal() { return fechaFinal; }
}