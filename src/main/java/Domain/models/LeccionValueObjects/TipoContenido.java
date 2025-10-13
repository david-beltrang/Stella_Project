package Domain.models.LeccionValueObjects;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Value Object para el tipo de contenido de una lección.
 * Garantiza que el valor sea uno de los tipos permitidos (TEORIA, PRACTICA, PREGUNTA, VIDEO, QUIZ).
 */
public record TipoContenido(String valor) {

    // CORRECCIÓN: Añadir "VIDEO" y "QUIZ" para que los datos de prueba de la BD sean válidos.
    private static final List<String> TIPOS_VALIDOS = Arrays.asList(
            "TEORIA",
            "PRACTICA",
            "PREGUNTA",
            "VIDEO",
            "QUIZ"
    );

    // Bloque Compact Constructor para validación
    public TipoContenido {
        Objects.requireNonNull(valor, "El tipo de contenido no puede ser nulo.");

        String tipoNormalizado = valor.toUpperCase();

        if (!TIPOS_VALIDOS.contains(tipoNormalizado)) {
            throw new IllegalArgumentException(
                    "El valor '" + valor + "' no es un tipo de contenido válido. Debe ser uno de: " + TIPOS_VALIDOS
            );
        }

        // Almacena el valor normalizado
        valor = tipoNormalizado;
    }

    // Método para obtener el valor normalizado
    public String valor() {
        return valor;
    }

    // Método estático para reconstruir el Value Object desde una cadena de la BD
    public static TipoContenido fromString(String valorCadena) {
        return new TipoContenido(valorCadena);
    }
}