package Domain.models.UsuarioValueObjects;

import Domain.exceptions.NombreInvalidoException;

public record Nombre (String valor) {

    public Nombre {
        if (!valor.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,50}$")) {
            throw new NombreInvalidoException(valor);
        }
    }
}