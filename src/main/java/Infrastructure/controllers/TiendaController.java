package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.tienda.*;
import Application.services.TiendaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import Infrastructure.ui.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TiendaController {
    private static final Logger logger = LoggerFactory.getLogger(TiendaController.class);

    private TiendaService tiendaService;
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private final ImageLoader imageLoader = new ImageLoader();
    private Function<Class<?>, Object> controllerFactory;

    // 🔹 Este id ahora se va a llenar con el usuario actual, no fijo en 1
    private int usuarioId = 1;
    private int saldoUsuario = 0;

    @FXML
    private AnchorPane root;

    @FXML
    private Button AccesorioBtn1;
    @FXML
    private Button AccesorioBtn2;
    @FXML
    private Button AccesorioBtn3;
    @FXML
    private Button AccesorioBtn4;
    @FXML
    private Button AccesorioBtn5;
    @FXML
    private Button AccesorioBtn6;
    @FXML
    private Button AccesorioBtn7;
    @FXML
    private Button AccesorioBtn8;
    @FXML
    private Button AccesorioBtn9;
    @FXML
    private Button AccesorioBtn10;
    @FXML
    private Button AccesorioBtn11;
    @FXML
    private Button AccesorioBtn12;
    @FXML
    private Button AccesorioBtn13;
    @FXML
    private Button AccesorioBtn14;
    @FXML
    private Button AccesorioBtn15;
    @FXML
    private Button AccesorioBtn16;

    @FXML
    private ImageView imgItem;

    private List<ItemTiendaResponse> items = new ArrayList<>();
    private ItemTiendaResponse itemSeleccionado;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    public TiendaController() {
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void setTiendaService(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    // 🔹 NUEVO: igualito a PrincipalController pero para tienda
    private void inicializarUsuario() {
        var usuario = AppServices.getUsuarioActual();
        if (usuario != null) {
            this.usuarioId = usuario.id();
            logger.info("Usuario activo en tienda: {} (id={})", usuario.nombre(), usuarioId);
        } else {
            logger.warn("No hay usuario activo. Se usará usuarioId={}", usuarioId);
        }
    }

    @FXML
    private void initialize() {
        logger.debug("Iniciando TiendaController...");

        // 🔹 Primero obtenemos el id real del usuario logueado
        inicializarUsuario();

        if (tiendaService == null) {
            logger.error("tiendaService es NULL. No se puede inicializar la tienda.");
            return;
        }

        try {
            saldoUsuario = tiendaService.obtenerSaldoUsuario(usuarioId);
            logger.info("Saldo cargado correctamente: {} pescaditos", saldoUsuario);
        } catch (Exception e) {
            saldoUsuario = 0;
            logger.error("Error obteniendo saldo del usuario {}", usuarioId, e);
        }

        cargarItemsDesdeBD();
        poblarBotonesConItems();

        imgItem.setImage(null);
        logger.debug("Tienda inicializada correctamente");
    }

    private void cargarItemsDesdeBD() {
        try {
            items = tiendaService.obtenerItemsTienda();
            logger.info("Se cargaron {} ítems desde la base de datos", items.size());
        } catch (Exception e) {
            logger.error("Error cargando items de la tienda", e);
            uiHelper.showError("Error cargando la tienda", e.getMessage());
        }
    }

    private void poblarBotonesConItems() {
        if (items == null || items.isEmpty()) {
            logger.warn("No hay ítems para poblar en los botones");
            return;
        }

        Button[] botones = {
                AccesorioBtn1, AccesorioBtn2, AccesorioBtn3, AccesorioBtn4,
                AccesorioBtn5, AccesorioBtn6, AccesorioBtn7, AccesorioBtn8,
                AccesorioBtn9, AccesorioBtn10, AccesorioBtn11, AccesorioBtn12,
                AccesorioBtn13, AccesorioBtn14, AccesorioBtn15, AccesorioBtn16
        };

        for (int i = 0; i < botones.length; i++) {
            Button btn = botones[i];
            if (btn == null)
                continue;

            if (i < items.size()) {
                ItemTiendaResponse item = items.get(i);
                btn.setDisable(false);
                btn.setOpacity(1.0);

                ImageView iv = extraerImageViewDeBoton(btn);
                if (iv != null) {
                    // Delegar carga de imagen al servicio (Separation of Concerns)
                    imageLoader.cargarImagen(iv, item.imagePath());
                }

                btn.setOnAction(e -> mostrarItem(item));
                btn.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2)
                        mostrarPopupProducto(item);
                });

                logger.debug("Botón {} configurado con item: {}", (i + 1), item.nombre());
            } else {
                btn.setDisable(true);
                btn.setOpacity(0.3);
            }
        }
    }

    private ImageView extraerImageViewDeBoton(Button btn) {
        if (btn.getGraphic() instanceof ImageView iv)
            return iv;
        return null;
    }

    private void mostrarItem(ItemTiendaResponse item) {
        if (item == null)
            return;

        this.itemSeleccionado = item;
        logger.debug("Item seleccionado: {} (precio: {})", item.nombre(), item.precio());

        try {
            ItemDetalleResponse detalle = tiendaService.obtenerDetalleItem(new ItemDetalleRequest(item.id()));

            String stellaPath = detalle.stellaImagePath();
            String pathParaMostrar = (stellaPath != null && !stellaPath.isBlank())
                    ? stellaPath
                    : item.imagePath();

            // Delegar carga de imagen al servicio (Separation of Concerns)
            if (!imageLoader.cargarImagen(imgItem, pathParaMostrar)) {
                uiHelper.showError("Error", "No se pudo cargar la imagen del item");
            }
            logger.debug("Mostrando imagen del item: {}", pathParaMostrar);

        } catch (Exception e) {
            logger.error("Error cargando detalle del ítem {}", item.id(), e);
            uiHelper.showError("Error cargando detalle del ítem", e.getMessage());
        }
    }

    private void mostrarPopupProducto(ItemTiendaResponse item) {
        logger.debug("Abriendo popup de detalle para: {}", item.nombre());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProductoTienda.fxml"));
            if (controllerFactory != null)
                loader.setControllerFactory(controllerFactory::apply);

            Parent popupRoot = loader.load();
            ProductoTiendaController ctrl = loader.getController();
            ItemDetalleResponse detalle = tiendaService.obtenerDetalleItem(new ItemDetalleRequest(item.id()));
            ctrl.setProducto(detalle);

            // 🔹 Configuración del popup con referencia al controlador principal
            Scene popupScene = new Scene(popupRoot, 1100, 750);
            popupScene.setFill(Color.TRANSPARENT);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // 💡 Guardamos referencia al controlador de tienda en el Stage
            popupStage.getProperties().put("controller", this);

            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();
            logger.debug("Popup de producto cerrado correctamente");

        } catch (Exception e) {
            logger.error("Error mostrando popup de producto", e);
            uiHelper.showError("Error mostrando producto", e.getMessage());
        }
    }

    @FXML
    private void onComprarItem() {
        if (itemSeleccionado == null) {
            uiHelper.showInfo("Selecciona un ítem", "Primero elige un producto.");
            return;
        }

        logger.info("Intentando comprar item: {} (Precio: {}, Saldo: {}, UsuarioId: {})",
                itemSeleccionado.nombre(), itemSeleccionado.precio(), saldoUsuario, usuarioId);

        if (itemSeleccionado.precio() > saldoUsuario) {
            logger.warn("Saldo insuficiente para comprar item {}: precio={}, saldo={}",
                    itemSeleccionado.nombre(), itemSeleccionado.precio(), saldoUsuario);
            uiHelper.showError(
                    "Pescaditos insuficientes",
                    "Necesitas " + itemSeleccionado.precio() +
                            " pescaditos, pero solo tienes " + saldoUsuario + ".");
            return;
        }

        try {
            ItemCompraRequest request = new ItemCompraRequest(itemSeleccionado.id());
            tiendaService.comprarItem(usuarioId, request);
            saldoUsuario -= itemSeleccionado.precio();

            logger.info("Compra exitosa. Nuevo saldo: {}", saldoUsuario);
            uiHelper.showInfo("Compra exitosa", "¡Has comprado " + itemSeleccionado.nombre() + "!");

        } catch (RuntimeException e) {
            logger.error("Error de lógica al comprar item {}", itemSeleccionado.id(), e);
            uiHelper.showError("No se pudo completar la compra", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al comprar item {}", itemSeleccionado.id(), e);
            uiHelper.showError("Error inesperado al comprar", e.getMessage());
        }
    }

    // 🔁 Método llamado desde el popup
    public void forzarCompraDesdePopup(ItemDetalleResponse producto) {
        if (producto == null || itemSeleccionado == null) {
            logger.warn("No hay item seleccionado o producto nulo al intentar comprar desde popup");
            return;
        }
        onComprarItem(); // Reutiliza la lógica principal
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        try {
            Navegacion nav = new Navegacion();
            nav.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al volver al inicio desde tienda", e);
            uiHelper.showError("Error al volver al inicio", e.getMessage());
        }
    }

    @FXML
    private void goForum() {
        try {
            navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al foro desde tienda", e);
            uiHelper.showError("Error al navegar al foro", e.getMessage());
        }
    }

    @FXML
    private void goProfile() {
        try {
            navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al perfil desde tienda", e);
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goPomodoro() {
        uiHelper.showInfo("Pomodoro", "Desde tienda aún no se ha conectado.");
    }

    @FXML
    private void cerrarSesion() {
        try {
            // Cierra la sesión actual (borra el usuario en memoria)
            Application.config.AppServices.cerrarSesion();

            // 🔹 Redirige al login usando el mismo Navigacion que usas para las demás
            // vistas
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);

            logger.info("Sesión cerrada correctamente. Redirigiendo al Login...");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión desde tienda", e);
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
        }
    }
}
