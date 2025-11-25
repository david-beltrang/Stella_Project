// Application/services/TiendaService.java
package Application.services;

import Application.dtos.tienda.*;
import Domain.models.Item;
import Domain.models.StellaItem;
import Domain.repositoriesInterfaces.*;

import java.util.List;


public class TiendaService {

    private final InterfazItemRepository itemRepo;
    private final InterfazStellaItemRepository stellaRepo;
    private final InterfazUsuarioItemRepository usuarioItemRepo;
    private final UsuarioStellaService usuarioStellaService;

    public TiendaService(
            InterfazItemRepository itemRepo,
            InterfazStellaItemRepository stellaRepo,
            InterfazUsuarioItemRepository usuarioItemRepo,
            UsuarioStellaService usuarioStellaService
    ) {
        this.itemRepo = itemRepo;
        this.stellaRepo = stellaRepo;
        this.usuarioItemRepo = usuarioItemRepo;
        this.usuarioStellaService = usuarioStellaService;
    }

    // === 1. Lista de items en tienda ===
    public List<ItemTiendaResponse> obtenerItemsTienda() {
        return itemRepo.findAll().stream()
                .map(item -> new ItemTiendaResponse(
                        item.getId(),
                        item.getNombre(),
                        item.getPrecio().valor(),
                        item.getImagePath()
                ))
                .toList();
    }

    // === 2. Detalle del item al hacer click ===
    public ItemDetalleResponse obtenerDetalleItem(ItemDetalleRequest request) {
        Item item = itemRepo.findById(request.itemId())
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        String stellaPath = stellaRepo.findByItemId(item.getId())
                .map(StellaItem::getImagePath)
                .orElse(null);

        return new ItemDetalleResponse(
                item.getNombre(),
                item.getDescripcion(),
                item.getPrecio().valor(),
                stellaPath
        );
    }

    // === 3. Comprar un item de la tienda ===
    public void comprarItem(int usuarioId, ItemCompraRequest request) {
        // Verificar que el item exista en la DataBase
        Item item = itemRepo.findById(request.itemId())
                .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        // Verficar que el usuario no lo tenga todavia
        if (usuarioItemRepo.tieneItem(usuarioId, request.itemId())) {
            throw new RuntimeException("Ya tienes este item");
        }

        int costo = item.getPrecio().valor();
        int saldo = usuarioItemRepo.obtenerPescaditos(usuarioId);

        // Verificar que el usuario tenga pescaditos suficientes
        if (saldo < costo) {
            throw new RuntimeException("Pescaditos insuficientes: necesitas " + costo + ", tienes " + saldo);
        }

        // Comprar el item
        usuarioItemRepo.comprarItem(usuarioId, request.itemId(), costo);

        // Si el item tiene un StellaItem asociado, cambiar automáticamente la Stella actual
        if (stellaRepo.findByItemId(request.itemId()).isPresent() && usuarioStellaService != null) {
            try {
                usuarioStellaService.cambiarStellaActual(request.itemId());
            } catch (Exception e) {
                // Log el error pero no fallar la compra
                org.slf4j.LoggerFactory.getLogger(TiendaService.class)
                        .warn("No se pudo cambiar la Stella actual después de la compra: {}", e.getMessage());
            }
        }
    }
    // === 4. Obtener saldo actual del usuario ===
    public int obtenerSaldoUsuario(int usuarioId) {
        try {
            return usuarioItemRepo.obtenerPescaditos(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo obtener el saldo del usuario", e);
        }
    }

}
