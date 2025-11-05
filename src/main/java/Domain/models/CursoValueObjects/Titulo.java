package Domain.models.CursoValueObjects;
import Domain.exceptions.curso.TituloInvalidoException;

public record Titulo (String valorTitulo) {
    public Titulo {
        if (valorTitulo == null) {
            throw new TituloInvalidoException(valorTitulo);
        }

        String trimmedValor = valorTitulo.trim();
        if (trimmedValor.length() > 255) {
            throw new IllegalArgumentException("El título no puede exceder los 255 caracteres.");
        }
        // Se asegura que el valor almacenado esté limpio y tenga el valor del título original sin problemas
        valorTitulo = trimmedValor;
    }

}

