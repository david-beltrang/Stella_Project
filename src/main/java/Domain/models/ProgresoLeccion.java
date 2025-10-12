package Domain.models;

import Domain.models.LeccionValueObjects.EstadoLeccion;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad que registra el progreso de un Usuario en una Lección.
 */
public class ProgresoLeccion {
    private Integer id;
    private final int usuarioId;
    private final int leccionId;
    private EstadoLeccion estado;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaCompletado;

    // ------------------ CONSTRUCTOR PRIVADO ----------------------

    // Este constructor privado es utilizado internamente por los Factory Methods (crearNuevo y reconstruir)
    private ProgresoLeccion(Integer id, int usuarioId, int leccionId, String estado,
                            LocalDateTime fechaInicio, LocalDateTime fechaCompletado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
        // La creación del record ya maneja la validación de la cadena 'estado'
        this.estado = new EstadoLeccion(estado);
        this.fechaInicio = fechaInicio;
        this.fechaCompletado = fechaCompletado;
    }

    // ------------------ FACTORY METHODS ----------------------

    /**
     * Crea un nuevo progreso inicial, típicamente con estado "NO_INICIADA".
     */
    public static ProgresoLeccion crearNuevo(int usuarioId, int leccionId) {
        return new ProgresoLeccion(null, usuarioId, leccionId, "NO_INICIADA", null, null);
    }

    /**
     * Reconstruye la entidad desde la base de datos (usado por el Repositorio).
     * ESTE ES EL MÉTODO QUE FALTABA.
     */
    public static ProgresoLeccion reconstruir(Integer id, int usuarioId, int leccionId, String estado,
                                              LocalDateTime fechaInicio, LocalDateTime fechaCompletado) {
        return new ProgresoLeccion(id, usuarioId, leccionId, estado, fechaInicio, fechaCompletado);
    }

    // ------------------ LÓGICA DE DOMINIO ----------------------

    /** Inicia la sesión de estudio. */
    public void iniciarSesion() {
        if (this.estado.esCompletada()) {
            throw new IllegalStateException("La lección ya está COMPLETADA.");
        }
        if (this.estado.valor().equals("NO_INICIADA")) {
            this.estado = new EstadoLeccion("EN_PROGRESO");
            this.fechaInicio = LocalDateTime.now();
        }
    }

    /** Marca la lección como completada. */
    public void completar() {
        if (this.estado.esCompletada()) {
            return;
        }
        this.estado = new EstadoLeccion("COMPLETADA");
        this.fechaCompletado = LocalDateTime.now();
    }

    // ------------------ GETTERS ----------------------

    public Integer getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public int getLeccionId() { return leccionId; }
    public EstadoLeccion getEstado() { return estado; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public LocalDateTime getFechaCompletado() { return fechaCompletado; }

    // Setter para la persistencia (si es necesario actualizar el ID tras un INSERT)
    public void setId(int id) { this.id = id; }
}