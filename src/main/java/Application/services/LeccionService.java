package Application.services;

import Application.dtos.sesionEstudio.LeccionDetalleResponse;
import Application.dtos.sesionEstudio.OpcionResponse;
import Application.dtos.sesionEstudio.PreguntaResponse;
import Domain.models.Leccion;
import Domain.models.Pregunta;
import Domain.models.ProgresoLeccion;
import Domain.repositoriesInterfaces.InterfazIntentoRepository;
import Domain.repositoriesInterfaces.InterfazLeccionRepository;
import Domain.repositoriesInterfaces.InterfazProgresoRepository;
import Domain.repositoriesInterfaces.InterfazPruebaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación para la gestión y consulta de la estructura de lecciones,
 * incluyendo el progreso del usuario en un contexto de curso/sección.
 */
public class LeccionService {

    private final InterfazLeccionRepository leccionRepository;
    private final InterfazProgresoRepository progresoRepository;
    private final InterfazIntentoRepository intentoRepository;
    private final InterfazPruebaRepository pruebaRepository;

    public LeccionService(
            InterfazLeccionRepository leccionRepository,
            InterfazProgresoRepository progresoRepository,
            InterfazIntentoRepository intentoRepository,
            InterfazPruebaRepository pruebaRepository
    ) {
        this.leccionRepository = leccionRepository;
        this.progresoRepository = progresoRepository;
        this.intentoRepository = intentoRepository;
        this.pruebaRepository = pruebaRepository;
    }

    /**
     * Obtiene todas las lecciones de una sección, incluyendo el estado de progreso del usuario.
     */
    public List<LeccionDetalleResponse> obtenerLeccionesPorSeccion(int usuarioId, int cursoId, int numeroSeccion) {

        List<Leccion> lecciones = interfazProgresoRepository.buscarPorCursoYSeccion(cursoId, numeroSeccion);

        if (lecciones.isEmpty()) {
            return new ArrayList<>();
        }

        return lecciones.stream()
                .map(leccion -> {
                    Optional<ProgresoLeccion> progresoOpt = interfazProgresoRepository.buscarPorUsuarioYLeccion(usuarioId, leccion.getId());

                    // Usa el getter del record EstadoLeccion y la cadena literal "NO_INICIADA"
                    String estado = progresoOpt.map(progreso -> progreso.getEstado().valor())
                            .orElse("NO_INICIADA");

                    List<PreguntaResponse> preguntasDto = null;
                    if (leccion.getTipoContenido().equals("PREGUNTA") || leccion.getTipoContenido().equals("PRACTICA")) {
                        List<Pregunta> preguntasDominio = interfazProgresoRepository.buscarPreguntasAsociadas(leccion.getId());

                        preguntasDto = preguntasDominio.stream()
                                .map(this::mapToPreguntaResponse)
                                .collect(Collectors.toList());
                    }

                    return new LeccionDetalleResponse(
                            leccion.getId(),
                            leccion.getTitulo(),
                            leccion.getNumeroOrden(),
                            leccion.getTipoContenido(),
                            leccion.getContenidoHtml(),
                            estado,
                            preguntasDto
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para mapear la entidad Pregunta a su DTO de Respuesta.
     * Aquí se ha eliminado el uso de op.esCorrecta().
     */
    private PreguntaResponse mapToPreguntaResponse(Pregunta pregunta) {
        // CORRECCIÓN: Quitamos op.esCorrecta() al crear el DTO OpcionResponse
        List<OpcionResponse> opciones = pregunta.getOpciones().stream()
                .map(op -> new OpcionResponse(op.getId(), op.getTexto()))
                .collect(Collectors.toList());

        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getEnunciado(),
                opciones
        );
    }
}