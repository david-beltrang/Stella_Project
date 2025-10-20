package Domain.models.CursoValueObjects;

import Domain.exceptions.curso.EstadoLeccionInvalidoException;

import java.util.Arrays;
import java.util.List;

/**
 * Value Object para el estado de progreso de una lección.
 * Garantiza que el valor sea uno de los definidos en la base de datos (progreso_leccion.estado).
 */
public record EstadoLeccion(String valor) {

    private static final List<String> ESTADOS_VALIDOS = Arrays.asList(
            "NO_INICIADA",
            "EN_PROGRESO",
            "COMPLETADA",
            "BLOQUEADA"
    );

    public EstadoLeccion {
        if (valor == null || valor.isBlank()) {
            throw new EstadoLeccionInvalidoException("El estado no puede ser nulo o vacío.");
        }

        String estadoNormalizado = valor.toUpperCase();

        if (!ESTADOS_VALIDOS.contains(estadoNormalizado)) {
            // Lanza la excepcion si la lección tiene un estado invalido
            throw new EstadoLeccionInvalidoException("El valor '" + valor + "' no es un estado de lección válido. Debe ser uno de: " + ESTADOS_VALIDOS);
        }

        // Almacena el valor normalizado
        valor = estadoNormalizado;
    }

    public boolean esCompletada() {
        return valor.equals("COMPLETADA");
    }

    public boolean esEnProgreso() {
        return valor.equals("EN_PROGRESO");
    }
}

