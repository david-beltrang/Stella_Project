package Domain.models.CursoValueObjects;

public record CursoId(int valor) {
    public CursoId {
        if (valor <= 0) {
            throw new IllegalArgumentException("El ID del curso debe ser positivo.");
        }
    }
}
