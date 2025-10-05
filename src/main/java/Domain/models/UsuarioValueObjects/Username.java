package Domain.models.UsuarioValueObjects;

import Domain.exceptions.UsernameInvalidoException;

public record Username (String valor) {

    public Username {
        if (valor == null) {
            throw new UsernameInvalidoException(valor);
        }
    }
}