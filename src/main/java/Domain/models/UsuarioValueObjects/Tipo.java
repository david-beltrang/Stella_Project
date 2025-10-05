package Domain.models.UsuarioValueObjects;
import Domain.exceptions.TipoInvalidoException;

public record Tipo (String valor) {

    public Tipo{

        if (!valor.equalsIgnoreCase("ADMIN") && !valor.equalsIgnoreCase("ESTUDIANTE")) {
            throw new TipoInvalidoException(valor);
        }
        if (valor == null) {
            throw new TipoInvalidoException(valor);
        }
    }


}
