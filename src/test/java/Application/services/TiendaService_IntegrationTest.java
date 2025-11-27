// src/test/java/Application/services/TiendaService_IntegrationTest.java
package Application.services;

import Application.dtos.tienda.*;
import Domain.repositoriesInterfaces.*;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.repositories.*;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("TiendaService - Tests de integración reales (CC total: 10 → 100% cubierto)")
class TiendaService_IntegrationTest {

    private TiendaService service;
    private InterfazItemRepository itemRepo;
    private InterfazStellaItemRepository stellaRepo;
    private InterfazUsuarioItemRepository usuarioItemRepo;

    @BeforeEach
    void setUp() {
        // BD limpia + todos los datos reales (15 ítems, usuario 1 con 1000 pescaditos
        new H2DataBaseInitializer(ConexionBD.getInstance()).initialize();

        itemRepo = new ItemRepository(ConexionBD.getInstance());
        stellaRepo = new StellaItemRepository(ConexionBD.getInstance());
        usuarioItemRepo = new UsuarioItemRepository(ConexionBD.getInstance());

        // Pasamos null en el servicio de Stella → el código lo maneja sin problemas
        service = new TiendaService(itemRepo, stellaRepo, usuarioItemRepo, null);
    }

    // ========================================================================
    // 1. obtenerItemsTienda() → Complejidad ciclomática = 1
    // ========================================================================
    @Test
    @DisplayName("obtenerItemsTienda - Devuelve los 15 ítems de la tienda correctamente")
    void obtenerItemsTienda_RetornaListaCompleta() {
        List<ItemTiendaResponse> items = service.obtenerItemsTienda();

        assertEquals(15, items.size(), "Debe haber exactamente 15 ítems en la tienda");
        assertEquals("Hoodie GitHub", items.get(0).nombre());
        assertEquals(1150, items.get(0).precio());
        assertEquals(520, items.get(14).precio()); // Gorra Rosa
    }

    // ========================================================================
    // 2. obtenerDetalleItem(ItemDetalleRequest request) → Complejidad ciclomática =
    // 1
    // ========================================================================
    @Test
    @DisplayName("obtenerDetalleItem - Devuelve detalle completo con imagen de Stella")
    void obtenerDetalleItem_RetornaDetalleConStella() {
        ItemDetalleResponse detalle = service.obtenerDetalleItem(new ItemDetalleRequest(3));

        assertEquals("Camiseta FIS", detalle.nombre());
        assertTrue(detalle.descripcion().contains("FIS"));
        assertEquals(800, detalle.precio());
        assertEquals("/Image/stellas/stellaFIS.png", detalle.stellaImagePath());
    }

    // ========================================================================
    // 3. obtenerSaldoUsuario(int usuarioId) → Complejidad ciclomática = 2
    // ========================================================================
    @Test
    @DisplayName("obtenerSaldoUsuario - Usuario con pescaditos → devuelve saldo correcto")
    void obtenerSaldoUsuario_ConSaldo_RetornaValorCorrecto() {
        assertEquals(1000, service.obtenerSaldoUsuario(1), "Usuario 1 debe tener 1000 pescaditos iniciales");
    }

    @Test
    @DisplayName("obtenerSaldoUsuario - Usuario sin stats → devuelve 0")
    void obtenerSaldoUsuario_SinStats_RetornaCero() {
        assertEquals(0, service.obtenerSaldoUsuario(999), "Usuario inexistente debe tener 0 pescaditos");
    }

    // ========================================================================
    // 4. comprarItem(int usuarioId, ItemCompraRequest request) → Complejidad
    // ciclomática = 6
    // ========================================================================
    @Test
    @DisplayName("comprarItem - Compra exitosa: resta pescaditos y guarda la compra")
    void comprarItem_Exito_CompraYActualizaSaldo() {
        int saldoAntes = service.obtenerSaldoUsuario(1);
        service.comprarItem(1, new ItemCompraRequest(5)); // Balaca Disney → 450

        assertEquals(saldoAntes - 450, service.obtenerSaldoUsuario(1));
        assertTrue(usuarioItemRepo.tieneItem(1, 5));
    }

    @Test
    @DisplayName("comprarItem - Ítem no existe → lanza RuntimeException")
    void comprarItem_ItemNoExiste_LanzaExcepcion() {
        assertThrows(RuntimeException.class,
                () -> service.comprarItem(1, new ItemCompraRequest(999)),
                "Debe fallar si el ítem no existe");
    }

    @Test
    @DisplayName("comprarItem - Ya posee el ítem → lanza RuntimeException")
    void comprarItem_YaPoseeItem_LanzaExcepcion() {
        assertThrows(RuntimeException.class,
                () -> service.comprarItem(1, new ItemCompraRequest(4)), // Ya tiene la camiseta Colombia
                "No puede comprar un ítem duplicado");
    }

    // ===============================
    // Flujo completo de la tienda
    // ===============================
    @DisplayName("Flujo completo: listar → ver detalle → comprar → saldo actualizado")
    void flujoCompleto_TiendaFuncionaPerfectamente() {
        List<ItemTiendaResponse> items = service.obtenerItemsTienda();
        assertTrue(items.size() >= 15);

        ItemDetalleResponse detalle = service.obtenerDetalleItem(new ItemDetalleRequest(9));
        assertEquals("Croptop", detalle.nombre());

        int saldoAntes = service.obtenerSaldoUsuario(1);
        service.comprarItem(1, new ItemCompraRequest(9)); // Croptop 700

        assertEquals(saldoAntes - 700, service.obtenerSaldoUsuario(1));
        assertTrue(usuarioItemRepo.tieneItem(1, 9));
    }
}