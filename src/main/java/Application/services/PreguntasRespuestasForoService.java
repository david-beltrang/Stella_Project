package Application.services;

import Application.dtos.foro.PreguntaForoDTO;
import Domain.models.PreguntaForo;
import Domain.models.RespuestaForo;
import Domain.repositoriesInterfaces.InterfazPreguntaForoRepository;
import Domain.repositoriesInterfaces.InterfazRespuestaForoRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PreguntasRespuestasForoService {

    private final InterfazPreguntaForoRepository preguntaRepository;
    private final InterfazRespuestaForoRepository respuestaRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PreguntasRespuestasForoService(InterfazPreguntaForoRepository preguntaRepository,
            InterfazRespuestaForoRepository respuestaRepository) {
        this.preguntaRepository = preguntaRepository;
        this.respuestaRepository = respuestaRepository;
    }

    // ============ Preguntas ============

    public void crearPregunta(int usuarioId, String titulo, String contenido) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido no puede estar vacío");
        }

        PreguntaForo pregunta = PreguntaForo.crear(usuarioId, titulo, contenido);
        preguntaRepository.guardar(pregunta);
    }

    public Optional<PreguntaForo> obtenerPregunta(int id) {
        return preguntaRepository.obtenerPorId(id);
    }

    public List<PreguntaForoDTO> listarTodasPreguntasConRespuestas() {
        List<PreguntaForo> preguntas = preguntaRepository.listarTodas();

        return preguntas.stream()
                .map(p -> {
                    int cantidadRespuestas = respuestaRepository.listarPorPregunta(p.getId()).size();
                    return new PreguntaForoDTO(
                            p.getId(),
                            p.getUsuarioId(),
                            "Usuario " + p.getUsuarioId(), // Idealmente obtener nombre real
                            p.getTitulo(),
                            p.getContenido(),
                            p.getFechaCreacion().format(formatter),
                            cantidadRespuestas);
                })
                .collect(Collectors.toList());
    }

    public void eliminarPregunta(int id) {
        preguntaRepository.eliminar(id);
    }

    // ============ Respuestas ============

    public void crearRespuesta(int preguntaId, int usuarioId, String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("La respuesta no puede estar vacía");
        }

        RespuestaForo respuesta = RespuestaForo.crear(preguntaId, usuarioId, contenido);
        respuestaRepository.guardar(respuesta);
    }

    public List<RespuestaForo> listarRespuestas(int preguntaId) {
        return respuestaRepository.listarPorPregunta(preguntaId);
    }

    public void eliminarRespuesta(int id) {
        respuestaRepository.eliminar(id);
    }
}
