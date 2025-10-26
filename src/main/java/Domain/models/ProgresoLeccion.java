package Domain.models;

import Domain.models.LeccionValueObjects.EstadoProgreso;

import java.time.Instant;

/**
 * Entidad que registra el progreso de un Usuario en una Lección.
 */
public class ProgresoLeccion {

    private final Integer id; // ID único del registro de progreso
    private final Integer usuarioId;
    private final Integer leccionId;

    // Campos mutables/actualizables
    private EstadoProgreso estado;
    private Instant fechaInicio;
    private Instant fechaCompletado; // Puede ser null

    /**
     * Constructor 1: Para cargar un registro existente desde el Repositorio (Carga Completa).
     */
    public ProgresoLeccion(Integer id, Integer usuarioId, Integer leccionId,
                           EstadoProgreso estado, Instant fechaInicio, Instant fechaCompletado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaCompletado = fechaCompletado;
    }

    /**
     * Constructor 2: Para crear un nuevo registro de progreso (Inicialización).
     * El ID es nulo y el estado inicial es EN_CURSO.
     */
    public ProgresoLeccion(Integer usuarioId, Integer leccionId) {
        this(
                null, // El ID se asigna al guardar en la DB por primera vez
                usuarioId,
                leccionId,
                EstadoProgreso.EN_PROGRESO,
                Instant.now(),
                null
        );
    }

    public ProgresoLeccion(Integer id, Integer usuarioId, Integer leccionId, EstadoProgreso estadoProgreso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
        this.estado = estadoProgreso;
        this.fechaInicio = Instant.now();
    }

    public ProgresoLeccion(Integer usuarioId, Integer leccionId, EstadoProgreso estadoProgreso) {
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
        this.estado = estadoProgreso;
    }

    // ------------------ LÓGICA DE DOMINIO ----------------------

    /**
     * Marca la lección como completada y registra la fecha de finalización.
     */
    public void marcarComoCompletada() {
        if (this.estado != EstadoProgreso.COMPLETADA) {
            this.estado = EstadoProgreso.COMPLETADA;
            this.fechaCompletado = Instant.now();
        }
    }

    // Getters
    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public Integer getLeccionId() { return leccionId; }
    public EstadoProgreso getEstado() { return estado; }
    public Instant getFechaInicio() { return fechaInicio; }
    public Instant getFechaCompletado() { return fechaCompletado; }
}