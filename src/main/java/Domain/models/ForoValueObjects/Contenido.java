package Domain.models.ForoValueObjects;

import Domain.exceptions.Foro.ContenidoInvalidoException;

public record Contenido(String texto) {
    public Contenido {
        if (texto == null || texto.trim().isEmpty() || texto.length() > 1000) {
            throw new ContenidoInvalidoException("El contenido del post es obligatorio y debe tener máximo 1000 caracteres");
        }
        texto = texto.trim();
    }
}