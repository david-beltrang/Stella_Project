package Application.dtos.tienda;

public record ItemTiendaResponse(
        int id,
        String nombre,
        int precio,
        String imagePath
) {}