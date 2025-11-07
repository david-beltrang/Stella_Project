/*
package Application.services;

import Application.dtos.leccion.ContenidoLeccionResponse;
import Application.dtos.leccion.MarcarLeccionCompletadaRequest;
import Application.dtos.leccion.SolicitarContenidoLeccionRequest;
import Application.dtos.sesionEstudio.*;
import Domain.models.LeccionValueObjects.EstadoProgreso;
import Domain.models.Leccion;
import Domain.models.ProgresoLeccion;
import Domain.repositoriesInterfaces.*;

/**
 * Servicio de aplicación para gestionar las sesiones de estudio, progreso, y la lógica de gamificación.
 */
/*
public class SesionEstudioService {

    private final InterfazLeccionRepository leccionRepository;
    private final InterfazProgresoRepository progresoRepository;
    private final InterfazUsuarioStatsRepository usuarioStatsRepository; // Si tuviéramos gamificación

    public SesionEstudioService(
            InterfazLeccionRepository leccionRepository,
            InterfazProgresoRepository progresoRepository,
            InterfazUsuarioStatsRepository usuarioStatsRepository
    ) {
        this.leccionRepository = leccionRepository;
        this.progresoRepository = progresoRepository;
        this.usuarioStatsRepository = usuarioStatsRepository;
    }

    /**
     * Caso de Uso 1: Servir el contenido de una lección.
     * Valida la secuencialidad y devuelve el contenido y las opciones de navegación.
     */

/*
    public ContenidoLeccionResponse servirContenido(
            Integer usuarioId,
            SolicitarContenidoLeccionRequest request
    ) throws IllegalAccessException { // Excepción si la regla de negocio es violada

        Integer leccionId = request.leccionId();
        Leccion leccionActual = leccionRepository.buscarPorId(leccionId);

        if (leccionActual == null) {
            throw new IllegalArgumentException("Lección no encontrada.");
        }

        // 1. REGLA CRÍTICA DE SECUENCIALIDAD: Verificar si está desbloqueada.
        if (!isLeccionDesbloqueada(usuarioId, leccionActual)) {
            // Lanza una excepción de Dominio/Aplicación que el Controlador debería manejar (ej: 403 Forbidden)
            throw new IllegalAccessException("La lección anterior no ha sido completada.");
        }

        // 2. Determinar la navegabilidad y el estado actual

        // Determinar si hay lección anterior
        boolean puedeRetroceder = leccionRepository.buscarLeccionAnterior(
                leccionActual.getCursoId(), leccionActual.getNumeroSeccion(), leccionActual.getNumeroOrden()
        ) != null;

        // Determinar si puede avanzar: requiere que la lección actual esté COMPLETA Y que haya una siguiente lección.
        boolean puedeAvanzar = this.isLeccionCompletada(usuarioId, leccionId) &&
                leccionRepository.buscarProximaLeccion(
                        leccionActual.getCursoId(), leccionActual.getNumeroSeccion(), leccionActual.getNumeroOrden()
                ) != null;

        // 3. Mapeo a Response DTO. Usando accessors de Record (.titulo() y .valor()).
        return new ContenidoLeccionResponse(
                leccionActual.getId(),
                leccionActual.getTituloValor(),
                leccionActual.getContenidoHtml(),
                leccionActual.getTipoContenido(),
                puedeAvanzar,
                puedeRetroceder
        );
    }

    /**
     * Caso de Uso 2: Marcar Lección como Completa.
     * Actualiza el progreso y dispara la lógica de gamificación.
     */

/*

    public SesionCompletadaResponse marcarLeccionComoCompletada(
            Integer usuarioId,
            MarcarLeccionCompletadaRequest request
    ) {
        Integer leccionId = request.leccionId();

        // 1. Lógica del Dominio: Actualizar Progreso
        ProgresoLeccion progreso = progresoRepository.buscarPorUsuarioYLeccion(usuarioId, leccionId);

        if (progreso == null) {
            // Si el progreso no existe, lo creamos como completado
            progreso = new ProgresoLeccion(usuarioId, leccionId, EstadoProgreso.PENDIENTE);
        }

        // Se usa el método del Dominio para asegurar que la Entidad maneje su propia mutación.
        progreso.marcarComoCompletada();

        // Persistir el cambio
        progresoRepository.guardar(progreso);

        // 2. Lógica de Gamificación (Simulación)
        int pescaditosGanados = 10;
        // Lógica real: llamar a usuarioStatsRepository.incrementarPescaditos(...)

        // 3. Devolver Response DTO
        return new SesionCompletadaResponse(
                true,
                "Lección marcada como completada. ¡Felicidades!",
                pescaditosGanados
        );
    }


    // ------------------- Métodos Auxiliares de Lógica de Negocio -------------------

    /**
     * Determina si el usuario ha completado la lección anterior para desbloquear la actual.
     */

/*

    private boolean isLeccionDesbloqueada(Integer usuarioId, Leccion leccionActual) {
        // La primera lección de la primera sección siempre está desbloqueada
        if (leccionActual.getNumeroOrden() == 1 && leccionActual.getNumeroSeccion() == 1) {
            return true;
        }

        // Buscar la lección inmediatamente anterior en la secuencia
        Leccion leccionAnterior = leccionRepository.buscarLeccionAnterior(
                leccionActual.getCursoId(), leccionActual.getNumeroSeccion(), leccionActual.getNumeroOrden()
        );

        // Si no hay una lección anterior (debería ser el caso solo de la primera lección, pero es un chequeo de seguridad)
        if (leccionAnterior == null) {
            return false;
        }

        // La lección está desbloqueada si la lección anterior está completada.
        return isLeccionCompletada(usuarioId, leccionAnterior.getId());
    }


    /**
     * Verifica el estado de una lección específica en el repositorio de progreso.
     */

/*
    private boolean isLeccionCompletada(Integer usuarioId, Integer leccionId) {
        ProgresoLeccion progreso = progresoRepository.buscarPorUsuarioYLeccion(usuarioId, leccionId);
        // Es completada si existe el registro Y el estado es COMPLETADA
        return progreso != null && progreso.getEstado() == EstadoProgreso.COMPLETADA;
    }
}

*/