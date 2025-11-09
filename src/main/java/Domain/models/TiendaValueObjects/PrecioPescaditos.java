// Domain/valueObjects/PrecioPescaditos.java
package Domain.models.TiendaValueObjects;

public record PrecioPescaditos(int valor) {
    public PrecioPescaditos {
        if (valor < 0) throw new IllegalArgumentException("Precio no puede ser negativo");
    }
}