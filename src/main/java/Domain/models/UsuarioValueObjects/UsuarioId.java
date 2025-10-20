package Domain.models.UsuarioValueObjects;

public record UsuarioId(int valor) {
    public UsuarioId {
        if (valor <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser positivo.");
        }
    }
}