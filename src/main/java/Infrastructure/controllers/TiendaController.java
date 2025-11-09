package Infrastructure.controllers;

import Application.dtos.tienda.ItemCompraRequest;
import Application.dtos.tienda.ItemDetalleRequest;
import Application.dtos.tienda.ItemDetalleResponse;
import Application.dtos.tienda.ItemTiendaResponse;
import Application.services.TiendaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;

public class TiendaController {

    // ========= Dependencias =========
    private final TiendaService tiendaService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // Usuario logueado (inyéctalo desde fuera)
    private int usuarioId;

    // ========= FXML =========
    @FXML private Pane root;

    @FXML private ListView<ItemTiendaResponse> listaItems;
    @FXML private Label lblNombreItem;
    @FXML private Label lblPrecioItem;
    @FXML private TextArea txtDescripcionItem;
    @FXML private ImageView imgItem;
    @FXML private Button btnComprar;

    // ========= Estado interno =========
    private ItemTiendaResponse itemSeleccionado;

    // ========= Constructor =========
    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    // ========= Inicialización =========
    @FXML
    private void initialize() {
        configurarListaItems();
        cargarItemsTienda();
        limpiarDetalle();
    }

    private void configurarListaItems() {
        listaItems.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ItemTiendaResponse item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.nombre() + " - " + item.precio() + " pescaditos");
                }
            }
        });

        listaItems.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            itemSeleccionado = newVal;
            if (newVal != null) {
                cargarDetalleItem(newVal.id());
            } else {
                limpiarDetalle();
            }
        });
    }

    // ========= Lógica de tienda =========
    private void cargarItemsTienda() {
        try {
            List<ItemTiendaResponse> items = tiendaService.obtenerItemsTienda();
            listaItems.getItems().setAll(items);
        } catch (Exception e) {
            uiHelper.showError("Error cargando la tienda", e.getMessage());
        }
    }

    private void cargarDetalleItem(int itemId) {
        try {
            ItemDetalleResponse detalle = tiendaService.obtenerDetalleItem(
                    new ItemDetalleRequest(itemId)
            );

            lblNombreItem.setText(detalle.nombre());
            lblPrecioItem.setText(detalle.precio() + " pescaditos");
            txtDescripcionItem.setText(detalle.descripcion());

            if (imgItem != null && detalle.stellaImagePath() != null && !detalle.stellaImagePath().isBlank()) {
                try {
                    Image image = new Image(getClass().getResourceAsStream(detalle.stellaImagePath()));
                    imgItem.setImage(image);
                } catch (Exception ignored) {
                    imgItem.setImage(null);
                }
            }

        } catch (Exception e) {
            uiHelper.showError("Error cargando detalle del ítem", e.getMessage());
        }
    }

    private void limpiarDetalle() {
        if (lblNombreItem != null) lblNombreItem.setText("");
        if (lblPrecioItem != null) lblPrecioItem.setText("");
        if (txtDescripcionItem != null) txtDescripcionItem.clear();
        if (imgItem != null) imgItem.setImage(null);
    }

    @FXML
    private void onComprarItem() {
        if (itemSeleccionado == null) {
            uiHelper.showInfo("Selecciona un ítem", "Primero elige un producto de la lista.");
            return;
        }

        try {
            ItemCompraRequest request = new ItemCompraRequest(itemSeleccionado.id());
            tiendaService.comprarItem(usuarioId, request);

            uiHelper.showInfo("Compra exitosa", "¡Has comprado " + itemSeleccionado.nombre() + "!");
        } catch (Exception e) {
            uiHelper.showError("No se pudo completar la compra", e.getMessage());
        }
    }

    // ========= Navegación =========
    @FXML
    private void onVolverAPrincipal() {
        navigator.goTo(
                "/fxml/Principal.fxml",   // ajusta la ruta si tu principal tiene otra
                "STELLA",
                controllerFactory,
                root
        );
    }
}
