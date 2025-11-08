// Application/dtos/item/ItemDetalleResponse.java
package Application.dtos.tienda;

public record ItemDetalleResponse(
        String nombre,
        String descripcion,
        int precio,
        String stellaImagePath
) {}