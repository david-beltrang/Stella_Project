package Domain.models.PruebaValueObjects;

import Domain.exceptions.Prueba.TipoPruebaInvalidoException;
import Domain.exceptions.curso.NivelCursoInvalidoException;

import java.util.Arrays;
import java.util.List;

public record TipoValueObject(String valorTipo) {
    private static final List<String> TIPOS_VALIDOS = Arrays.asList("SECCIONAL", "EXAMEN");

    public TipoValueObject {
        if (valorTipo == null || valorTipo.isBlank()) {
            throw new NivelCursoInvalidoException("El tipo de prueba no puede ser nulo o vacío.");
        }

        String nivelNormalizado = valorTipo.toUpperCase();

        if (!TIPOS_VALIDOS.contains(nivelNormalizado)) {
            // Lanza la excepcion si el Curso tiene un Nivel invalido
            throw new TipoPruebaInvalidoException("El valor '" + valorTipo + "' no es un tipo de prueba válido. Debe ser uno de: " + TIPOS_VALIDOS);
        }

        // Almacena el valor normalizado
        valorTipo = nivelNormalizado;
    }
}
