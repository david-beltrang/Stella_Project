package Domain.models.CursoValueObjects;
import Domain.exceptions.curso.TituloInvalidoException;

public record Titulo (String valorTitulo) {
    public Titulo {
        if (valorTitulo == null) {
            throw new TituloInvalidoException(valorTitulo);
        }
    }
}
