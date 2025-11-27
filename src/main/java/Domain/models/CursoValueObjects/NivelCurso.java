package Domain.models.CursoValueObjects;

import Domain.exceptions.curso.NivelCursoInvalidoException;
import java.util.Arrays;
import java.util.List;

/**
 * Value Object para el nivel de un curso.
 * Alineado con la restricción CHECK de la tabla curso.nivel.
 */
public record NivelCurso(String valor) {

    private static final List<String> NIVELES_VALIDOS = Arrays.asList(
            "BASICO",
            "INTERMEDIO",
            "AVANZADO"
    );

    public NivelCurso {
        if (valor == null || valor.isBlank()) {
            throw new NivelCursoInvalidoException("El nivel del curso no puede ser nulo o vacío.");
        }

        String nivelNormalizado = valor.toUpperCase();

        if (!NIVELES_VALIDOS.contains(nivelNormalizado)) {
            // Lanza la excepcion si el Curso tiene un Nivel invalido
            throw new NivelCursoInvalidoException("El valor '" + valor + "' no es un nivel de curso válido. Debe ser uno de: " + NIVELES_VALIDOS);
        }

        // Almacena el valor normalizado
        valor = nivelNormalizado;
    }
}
