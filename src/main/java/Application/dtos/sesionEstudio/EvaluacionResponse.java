package Application.dtos.sesionEstudio;

/**
 * dto de respuesta que indica el resultado de la evaluación de una pregunta.
 */
public record EvaluacionResponse(
        boolean esCorrecta,
        String feedback,
        int puntosObtenidos // Pescaditos ganados en este paso
) {}
