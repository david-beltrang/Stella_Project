package Domain.models;

import Application.dtos.curso.ProgresoLeccionId;
import Domain.models.CursoValueObjects.EstadoProgreso;
import Domain.models.CursoValueObjects.LeccionId;
import Domain.models.UsuarioValueObjects.UsuarioId;

import java.time.Instant;

/**
 * Entidad que registra el progreso de un Usuario en una Lección.
 */
public class ProgresoLeccion {
    // Campos inmutables
    private final ProgresoLeccionId id; // ID único del registro de progreso
    private final UsuarioId usuarioId;
    private final LeccionId leccionId;

    // Campos mutables/actualizables
    private EstadoProgreso estado;
    private Instant fechaInicio;
    private Instant fechaCompletado; // Puede ser null

    /**
     * Constructor 1: Para cargar un registro existente desde el Repositorio (Carga Completa).
     */
    public ProgresoLeccion(ProgresoLeccionId id, UsuarioId usuarioId, LeccionId leccionId,
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
    public ProgresoLeccion(UsuarioId usuarioId, LeccionId leccionId) {
        this(
                null, // El ID se asigna al guardar en la DB por primera vez
                usuarioId,
                leccionId,
                EstadoProgreso.EN_PROGRESO,
                Instant.now(),
                null
        );
    }

    public ProgresoLeccion(ProgresoLeccionId id, UsuarioId usuarioId, LeccionId leccionId, EstadoProgreso estadoProgreso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.leccionId = leccionId;
        this.estado = estadoProgreso;
        this.fechaInicio = Instant.now();
    }

    public ProgresoLeccion(UsuarioId usuarioId, LeccionId leccionId, EstadoProgreso estadoProgreso) {
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
    public ProgresoLeccionId getId() { return id; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public LeccionId getLeccionId() { return leccionId; }
    public EstadoProgreso getEstado() { return estado; }
    public Instant getFechaInicio() { return fechaInicio; }
    public Instant getFechaCompletado() { return fechaCompletado; }
}