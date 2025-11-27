//package Infrastructure.test_temporal;
//
//import Application.dtos.tienda.ItemCompraRequest;
//import Application.dtos.tienda.ItemDetalleRequest;
//import Application.services.TiendaService;
//import Domain.repositoriesInterfaces.InterfazItemRepository;
//import Domain.repositoriesInterfaces.InterfazStellaItemRepository;
//import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
//import Infrastructure.persistence.ConexionBD;
//import Infrastructure.persistence.H2DataBaseInitializer;
//import Infrastructure.persistence.IConexionBD;
//import Infrastructure.repositories.ItemRepository;
//import Infrastructure.repositories.StellaItemRepository;
//import Infrastructure.repositories.UsuarioItemRepository;
//
//public class TiendaServiceTest {
//    public static void main(String[] args) {
//        IConexionBD connMgr = new ConexionBD();
//        new H2DataBaseInitializer(connMgr).initialize();
//
//        InterfazItemRepository itemRepository = new ItemRepository(connMgr);
//        InterfazStellaItemRepository stellaRepository = new StellaItemRepository(connMgr);
//        InterfazUsuarioItemRepository usuarioItemRepository = new UsuarioItemRepository(connMgr);
//        TiendaService tiendaService = new TiendaService(itemRepository, stellaRepository, usuarioItemRepository);
//
//        int usuarioId = 1;
//
//        // === 1. Lista de items ===
//        System.out.println("1. LISTA DE ITEMS EN TIENDA");
//        tiendaService.obtenerItemsTienda().forEach(item -> {
//            String imagePath = item.imagePath() != null ? item.imagePath() : "Sin imagen";
//            System.out.printf("   • [%d] %s - %d pescaditos | Imagen: %s%n",
//                    item.id(), item.nombre(), item.precio(), imagePath);
//        });
//        System.out.println();
//
//        // === 2. Detalle de item ===
//        System.out.println("2. DETALLE DEL ITEM (ID=1)");
//        var detalle = tiendaService.obtenerDetalleItem(new ItemDetalleRequest(1));
//        System.out.println("   Nombre: " + detalle.nombre());
//        System.out.println("   Descripción: " + detalle.descripcion());
//        System.out.println("   Precio: " + detalle.precio());
//        System.out.println("   Imagen en Tienda: " +
//                (detalle.stellaImagePath() != null ? "Existe la imagen" : "Sin imagen"));
//        System.out.println("   Stella: " +
//                (detalle.stellaImagePath() != null ? "Sí → " + detalle.stellaImagePath() : "No"));
//        System.out.println();
//
//        // === 3. Comprar item ===
//        System.out.println("3. COMPRAR ITEM");
//        try {
//            tiendaService.comprarItem(usuarioId, new ItemCompraRequest(2));
//            System.out.println("   ÉXITO: Compra realizada");
//        } catch (Exception e) {
//            System.out.println("   ERROR: " + e.getMessage());
//        }
//    }
//}