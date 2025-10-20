package Domain.models.CursoValueObjects;

public record LeccionId(int valor) {
    public LeccionId {
        if (valor <= 0) {
            throw new IllegalArgumentException("El ID de la lección debe ser positivo.");
        }
    }
}
