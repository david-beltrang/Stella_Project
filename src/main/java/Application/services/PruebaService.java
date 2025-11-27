package Application.services;

import Application.dtos.Prueba.*;
import Domain.models.Intento;
import Domain.models.Respuesta;
import Domain.models.PruebaValueObjects.Puntaje;
import Domain.repositoriesInterfaces.*;

import java.util.*;

public class PruebaService {
    private final InterfazPruebaRepository pruebaRepository;
    private final InterfazIntentoRepository intentoRepository;
    private final InterfazOpcionRepository opcionRepository; // NUEVO

    public PruebaService(
            InterfazPruebaRepository pruebaRepository,
            InterfazIntentoRepository intentoRepository,
            InterfazOpcionRepository opcionRepository) {
        this.pruebaRepository = pruebaRepository;
        this.intentoRepository = intentoRepository;
        this.opcionRepository = opcionRepository;
    }

    public PruebaResponse obtenerQuizPorSeccion(int seccionId) {
        return pruebaRepository.encontrarPorSeccionId(seccionId)
                .orElseThrow(() -> new RuntimeException("No hay prueba para esta sección"));
    }

    public IntentoResponse crearIntento(IntentoRequest request) {
        //Obtener respuestas correctas desde el repo - la clave es el id de pregunta, y el valor el id de opcion correcta
        Map<Integer, Integer> correctas = opcionRepository.encontrarOpcionesCorrectasPorPruebaId(request.pruebaId());

        int aciertos = 0;
        //En esta lista se almacenan las respuestas del usuario
        List<Respuesta> respuestas = new ArrayList<>();

        //Se recorre la lista de respuestas del request de intento del usuario
        for (RespuestaRequest rr : request.respuestas()) {
            //Se obtiene el id de la pregunta
            int preguntaId = rr.preguntaId();
            //Se obtiene el id de la opcion elegida
            int opcionElegida = rr.opcionSeleccionadaId();

            //Si la opcion correcta de la pregunta con el id actual no es nula y es igual a la que selecconó el usuario
            //entonces hay un acierto, el contador de aciertos aumenta.
            if (correctas.get(preguntaId) != null && correctas.get(preguntaId) == opcionElegida) {
                aciertos++;
            }

            //Se usa el factory method de la clase entity de Respuesta para crear una respuesta y poderla meter en la bd despues
            respuestas.add(Respuesta.crear(null, preguntaId, opcionElegida));
        }

        double puntaje = request.respuestas().isEmpty() ? 0.0 : (double) aciertos / request.respuestas().size() * 100;
        Intento intento = Intento.crear(request.usuarioId(), request.pruebaId(), new Puntaje(puntaje));
        //Se guarda el intento en la BD
        Intento guardado = intentoRepository.guardar(intento);

        // Actualizar respuestas con el id del intento
        List<Respuesta> respuestasActualizadas = new ArrayList<>();
        for (Respuesta r : respuestas) {
            respuestasActualizadas.add(
                    Respuesta.crear(guardado.getId(), r.getPreguntaId(), r.getOpcionSeleccionadaId())
            );
        }
        intentoRepository.guardarRespuestas(respuestasActualizadas);
        return new IntentoResponse(
                guardado.getId(),
                puntaje,
                request.respuestas().size(),
                aciertos,
                aciertos == request.respuestas().size() ? "¡Perfecto!" : "Buen intento"
        );

    }
}