package Domain.models.UsuarioValueObjects;

import Domain.exceptions.CorreoInvalidoException;

public record Correo (String valor) {

    public Correo {
        if (valor == null || !valor.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new CorreoInvalidoException(valor);
        }
    }
}