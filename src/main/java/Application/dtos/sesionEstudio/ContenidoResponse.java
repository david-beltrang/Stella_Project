package Application.dtos.sesionEstudio;

import java.util.List;

/**
 * dto de respuesta que contiene el material de estudio (texto o preguntas).
 * el campo 'datos' puede ser el contenido HTML/texto o una lista de PreguntaResponse.
 */
public record ContenidoResponse(
        String tipoContenido, // VIDEO, TEORIA, PRACTICA
        String tituloLeccion,
        String progresoEstado, // NO_INICIADA, EN_PROGRESO, COMPLETADA
        List<PreguntaResponse> preguntas, // Null si el tipoContenido es VIDEO/TEORIA
        String contenidoTextoHTML // Null si el tipoContenido es PRACTICA (prueba)
) {}