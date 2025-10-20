package Domain.models.CursoValueObjects;

public record Titulo(String valor) {
    public Titulo {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        String trimmedValor = valor.trim();
        if (trimmedValor.length() > 255) {
            throw new IllegalArgumentException("El título no puede exceder los 255 caracteres.");
        }
        // Se asegura que el valor almacenado esté limpio (trimmed)
        valor = trimmedValor;
    }

    public String getValor() {
        return valor;
    }
}
