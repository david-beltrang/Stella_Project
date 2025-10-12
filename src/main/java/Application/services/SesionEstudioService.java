package Application.services;

import Application.dtos.internal.*;
import Application.dtos.sesionEstudio.*;
import Domain.models.ProgresoLeccion;
import Domain.models.SesionEstudio;
import Domain.models.LeccionValueObjects.EstadoLeccion;
import Domain.repositoriesInterfaces.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para gestionar las sesiones de estudio, progreso, y la lógica de gamificación.
 */
public class SesionEstudioService {

    private final InterfazProgresoLeccionRepository progresoRepo;
    private final InterfazLeccionRepository leccionRepo;
    private final InterfazPruebaRepository pruebaRepo;
    private final InterfazIntentoRepository intentoRepo;
    private final InterfazUsuarioStatsRepository statsRepo;

    public SesionEstudioService(
            InterfazProgresoLeccionRepository progresoRepo,
            InterfazLeccionRepository leccionRepo,
            InterfazPruebaRepository pruebaRepo,
            InterfazIntentoRepository intentoRepo,
            InterfazUsuarioStatsRepository statsRepo) {
        this.progresoRepo = progresoRepo;
        this.leccionRepo = leccionRepo;
        this.pruebaRepo = pruebaRepo;
        this.intentoRepo = intentoRepo;
        this.statsRepo = statsRepo;
    }

    /**
     * Inicia o reanuda una sesión de estudio para una lección específica.
     * @param request DTO con usuarioId y leccionId.
     * @return ContenidoResponse con el material a mostrar.
     */
    public ContenidoResponse iniciarSesion(LeccionRequest request) {
        // 1. Obtener Progreso (o crear si no existe)
        ProgresoLeccion progreso = progresoRepo
                .buscarPorUsuarioYLeccion(request.usuarioId(), request.leccionId())
                .orElseGet(() -> ProgresoLeccion.crearNuevo(request.usuarioId(), request.leccionId()));

        // 2. Aplicar lógica de Dominio (actualizar estado, fecha inicio)
        try {
            progreso.iniciarSesion();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("No se puede iniciar la lección: " + e.getMessage());
        }

        // 3. Guardar el progreso actualizado (si es nuevo o cambia de NO_INICIADA)
        progresoRepo.guardar(progreso);

        // 4. Obtener Contenido de la Lección
        ContenidoLeccionInternal contenidoDto = leccionRepo.obtenerContenido(request.leccionId())
                .orElseThrow(() -> new IllegalArgumentException("Lección no encontrada."));

        // 5. Mapear Contenido a Respuesta
        return mapContenidoToResponse(contenidoDto, progreso.getEstado().valor());
    }

    /**
     * Finaliza la sesión de estudio, actualiza el progreso y la gamificación.
     * @param request DTO con tiempos y IDs.
     */
    public void finalizarSesion(FinalizarSesionRequest request) {
        // 1. Lógica del Progreso (Dominio)
        ProgresoLeccion progreso = progresoRepo
                .buscarPorUsuarioYLeccion(request.usuarioId(), request.leccionId())
                .orElseThrow(() -> new IllegalArgumentException("Progreso no encontrado."));

        // Marcar como completada (Lógica de Dominio)
        progreso.completar();
        progresoRepo.guardar(progreso);

        // 2. Lógica de la Sesión de Estudio (Registro de actividad)
        SesionEstudio sesion = SesionEstudio.iniciar(request.usuarioId());
        sesion.finalizar(request.tiempoEstudioSegundos() / 60, request.tiempoDescansoSegundos() / 60);
        // NOTA: Se requeriría un InterfazSesionEstudioRepository para guardar la sesión.

        // 3. Lógica de Gamificación (UsuarioStats)
        int pescaditosGanados = calcularPescaditos(request.tiempoEstudioSegundos());
        statsRepo.actualizarPescaditos(request.usuarioId(), pescaditosGanados);
        // El error de la columna DIAS_RACHA se corrige en UsuarioStatsRepository
        statsRepo.actualizarRacha(request.usuarioId(), 1);

        // 4. (Opcional) Llamar a otro servicio para guardar la sesión si fuera necesario.
    }

    /**
     * Evalúa la respuesta de una pregunta.
     * @param request DTO con la selección del usuario.
     * @return EvaluaciónResponse con el feedback y puntos.
     */
    public EvaluacionResponse evaluarRespuesta(EvaluarRespuestaRequest request) {
        // 1. Obtener la pregunta y sus opciones (incluida la correcta)
        List<PreguntaConOpcionesInternal> preguntas = pruebaRepo.obtenerPreguntasPorId(request.preguntaId());
        PreguntaConOpcionesInternal pregunta = preguntas.stream().filter(p -> p.preguntaId() == request.preguntaId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pregunta no encontrada."));

        // 2. Encontrar la opción seleccionada y la correcta
        OpcionDetalleInternal seleccion = pregunta.opciones().stream()
                .filter(o -> o.opcionId() == request.opcionSeleccionadaId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Opción no válida."));

        boolean esCorrecta = seleccion.esCorrecta();
        int puntos = esCorrecta ? 5 : 0;
        String feedback = esCorrecta ? "¡Correcto! Ganaste 5 pescaditos." : "Incorrecto. Inténtalo de nuevo.";

        // 3. Registrar Intento
        ContenidoLeccionInternal contenidoLeccion = leccionRepo.obtenerContenido(request.leccionId())
                .orElseThrow(() -> new IllegalArgumentException("Lección para evaluación no encontrada."));

        int pruebaId = contenidoLeccion.getPruebaIdPrimitivo(); // ID de la prueba (0 si es null)

        // CORRECCIÓN CLAVE: Solo registrar intento si pruebaId es válido (evita Foreign Key Violation)
        if (pruebaId > 0) {
            int intentoId = intentoRepo.guardarIntento(new IntentoInternal(
                    null, request.usuarioId(), pruebaId, esCorrecta ? 100.0 : 0.0, LocalDateTime.now()
            ));

            // 4. Registrar Respuesta
            intentoRepo.guardarRespuesta(new RespuestaInternal(
                    null, intentoId, request.preguntaId(), request.opcionSeleccionadaId()
            ));
        } else {
            System.err.println("Advertencia: Intento de evaluar pregunta sin pruebaId válido (ID: " + request.leccionId() + "). No se registrará el intento.");
        }


        // 5. Actualizar Pescaditos inmediatamente si es correcto
        if (esCorrecta) {
            statsRepo.actualizarPescaditos(request.usuarioId(), puntos);
        }

        return new EvaluacionResponse(esCorrecta, feedback, puntos);
    }

    // --- Métodos de Mapeo y Lógica Auxiliar ---

    private int calcularPescaditos(int segundosEstudio) {
        // Regla simple de gamificación: 10 pescaditos por cada 5 minutos de estudio
        return (segundosEstudio / 300) * 10;
    }

    private ContenidoResponse mapContenidoToResponse(ContenidoLeccionInternal dto, String estadoProgreso) {
        List<PreguntaResponse> preguntas = new ArrayList<>();
        String contenidoHTML = null;

        // Se usa el método getPruebaIdPrimitivo() que devuelve 0 si es null
        if (dto.tipoContenido().valor().equals("PRACTICA") && dto.getPruebaIdPrimitivo() > 0) {
            // Lógica para cargar las preguntas de la prueba
            List<PreguntaConOpcionesInternal> dtos = pruebaRepo.obtenerPreguntas(dto.getPruebaIdPrimitivo());

            for (PreguntaConOpcionesInternal pDto : dtos) {
                // Mapear DTO interno a DTO de respuesta (excluyendo 'esCorrecta')
                List<OpcionResponse> opciones = pDto.opciones().stream()
                        .map(o -> new OpcionResponse(o.opcionId(), o.texto()))
                        .toList();

                preguntas.add(new PreguntaResponse(pDto.preguntaId(), pDto.enunciado(), opciones));
            }

        } else if (!dto.tipoContenido().valor().equals("PRACTICA")) {
            contenidoHTML = dto.contenido();
        }

        return new ContenidoResponse(
                dto.tipoContenido().valor(),
                dto.titulo(),
                estadoProgreso,
                preguntas.isEmpty() ? null : preguntas,
                contenidoHTML
        );
    }
}