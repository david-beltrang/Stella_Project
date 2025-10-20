package Application.dtos.curso;

public record ProgresoLeccionId(int valor) {

    // Constructor compacto para incluir la validación de la regla de negocio
    public ProgresoLeccionId {
        if (valor <= 0) {
            throw new IllegalArgumentException("El ID de progreso de la lección debe ser un número positivo.");
        }
    }

    // El método 'valor()' actúa como el getter.
}