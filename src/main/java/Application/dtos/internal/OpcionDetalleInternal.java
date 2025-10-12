package Application.dtos.internal;

/**
 * dto interno que incluye el estado de corrección de la opción (usado en el backend para evaluar).
 */
public record OpcionDetalleInternal(
        int opcionId,
        String texto,
        boolean esCorrecta
) {}