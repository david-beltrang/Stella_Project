package Infrastructure.controllers;

import Application.config.AppServices;            // 🔹 NUEVO
import Application.dtos.tienda.*;
import Application.services.TiendaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TiendaController {

    private TiendaService tiendaService;
    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // 🔹 Este id ahora se va a llenar con el usuario actual, no fijo en 1
    private int usuarioId = 1;
    private int saldoUsuario = 0;

    @FXML private AnchorPane root;

    @FXML private Button AccesorioBtn1;
    @FXML private Button AccesorioBtn2;
    @FXML private Button AccesorioBtn3;
    @FXML private Button AccesorioBtn4;
    @FXML private Button AccesorioBtn5;
    @FXML private Button AccesorioBtn6;
    @FXML private Button AccesorioBtn7;
    @FXML private Button AccesorioBtn8;
    @FXML private Button AccesorioBtn9;
    @FXML private Button AccesorioBtn10;
    @FXML private Button AccesorioBtn11;
    @FXML private Button AccesorioBtn12;

    @FXML private ImageView imgItem;

    private List<ItemTiendaResponse> items = new ArrayList<>();
    private ItemTiendaResponse itemSeleccionado;

    public TiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    public TiendaController() {}

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
            System.out.println("[TIENDA] Usuario activo: " + usuario.nombre() +
                    " (id=" + usuarioId + ")");
        } else {
            System.err.println("[TIENDA] No hay usuario activo. Se usará usuarioId=" + usuarioId);
        }
    }

    @FXML
    private void initialize() {
        System.out.println("[INIT] Iniciando TiendaController...");

        // 🔹 Primero obtenemos el id real del usuario logueado
        inicializarUsuario();

        if (tiendaService == null) {
            System.out.println("[INIT] tiendaService es NULL ");
            return;
        }

        try {
            saldoUsuario = tiendaService.obtenerSaldoUsuario(usuarioId);
            System.out.println("[INIT] Saldo cargado correctamente -> " + saldoUsuario + " pescaditos 🪙");
        } catch (Exception e) {
            saldoUsuario = 0;
            System.out.println("[INIT] Error obteniendo saldo: " + e.getMessage());
        }

        cargarItemsDesdeBD();
        poblarBotonesConItems();

        imgItem.setImage(null);
        System.out.println("[INIT] Tienda inicializada correctamente ");
    }

    private void cargarItemsDesdeBD() {
        try {
            items = tiendaService.obtenerItemsTienda();
            System.out.println("[BD] Se cargaron " + items.size() + " ítems desde la base de datos.");
        } catch (Exception e) {
            uiHelper.showError("Error cargando la tienda", e.getMessage());
            System.out.println("[BD] Error: " + e.getMessage());
        }
    }

    private void poblarBotonesConItems() {
        if (items == null || items.isEmpty()) {
            System.out.println("[BOTONES] No hay ítems para poblar.");
            return;
        }

        Button[] botones = {
                AccesorioBtn1, AccesorioBtn2, AccesorioBtn3, AccesorioBtn4,
                AccesorioBtn5, AccesorioBtn6, AccesorioBtn7, AccesorioBtn8,
                AccesorioBtn9, AccesorioBtn10, AccesorioBtn11, AccesorioBtn12
        };

        for (int i = 0; i < botones.length; i++) {
            Button btn = botones[i];
            if (btn == null) continue;

            if (i < items.size()) {
                ItemTiendaResponse item = items.get(i);
                btn.setDisable(false);
                btn.setOpacity(1.0);

                ImageView iv = extraerImageViewDeBoton(btn);
                if (iv != null)
                    cargarImagenEnImageView(iv, item.imagePath(), "[TIENDA-BOTON] ");

                btn.setOnAction(e -> mostrarItem(item));
                btn.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) mostrarPopupProducto(item);
                });

                System.out.println("[BOTONES] Botón " + (i+1) + " -> " + item.nombre());
            } else {
                btn.setDisable(true);
                btn.setOpacity(0.3);
            }
        }
    }

    private ImageView extraerImageViewDeBoton(Button btn) {
        if (btn.getGraphic() instanceof ImageView iv) return iv;
        return null;
    }

    private void mostrarItem(ItemTiendaResponse item) {
        if (item == null) return;

        this.itemSeleccionado = item;
        System.out.println("[ITEM] Seleccionado: " + item.nombre() + " (precio: " + item.precio() + ")");

        try {
            ItemDetalleResponse detalle =
                    tiendaService.obtenerDetalleItem(new ItemDetalleRequest(item.id()));

            String stellaPath = detalle.stellaImagePath();
            String pathParaMostrar = (stellaPath != null && !stellaPath.isBlank())
                    ? stellaPath
                    : item.imagePath();

            cargarImagenEnImageView(imgItem, pathParaMostrar, "[TIENDA-DETALLE] ");
            System.out.println("[ITEM] Mostrando imagen: " + pathParaMostrar);

        } catch (Exception e) {
            uiHelper.showError("Error cargando detalle del ítem", e.getMessage());
            System.out.println("[ITEM] Error detalle: " + e.getMessage());
        }
    }

    private void mostrarPopupProducto(ItemTiendaResponse item) {
        System.out.println("[POPUP] Abriendo detalle para " + item.nombre());
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
            System.out.println("[POPUP] Cerrado correctamente ");

        } catch (Exception e) {
            uiHelper.showError("Error mostrando producto", e.getMessage());
            System.out.println("[POPUP]  Error: " + e.getMessage());
        }
    }

    private void cargarImagenEnImageView(ImageView destino, String rawPath, String prefixLog) {
        if (destino == null) return;
        String path = rawPath == null ? "" : rawPath.trim();
        if (path.isBlank()) return;

        if (!path.startsWith("/")) path = "/" + path;
        URL url = getClass().getResource(path);
        if (url != null) {
            Image image = new Image(url.toExternalForm());
            destino.setImage(image);
            destino.setPreserveRatio(true);
            if (destino.getFitWidth() <= 0 && destino.getFitHeight() <= 0) {
                destino.setFitWidth(150);
                destino.setFitHeight(150);
            }
            System.out.println(prefixLog + " Imagen cargada: " + path);
        } else {
            System.out.println(prefixLog + "  Imagen no encontrada: " + path);
            uiHelper.showError("Imagen no encontrada", "No se encontró la imagen en: " + path);
        }
    }

    @FXML
    private void onComprarItem() {
        if (itemSeleccionado == null) {
            uiHelper.showInfo("Selecciona un ítem", "Primero elige un producto.");
            return;
        }

        System.out.println("[COMPRA] Intentando comprar " + itemSeleccionado.nombre() +
                " (Precio: " + itemSeleccionado.precio() + ", Saldo: " + saldoUsuario +
                ", UsuarioId: " + usuarioId + ")");

        if (itemSeleccionado.precio() > saldoUsuario) {
            System.out.println("[COMPRA]  Saldo insuficiente. Falta dinero.");
            uiHelper.showError(
                    "Pescaditos insuficientes",
                    "Necesitas " + itemSeleccionado.precio() +
                            " pescaditos, pero solo tienes " + saldoUsuario + "."
            );
            return;
        }

        try {
            ItemCompraRequest request = new ItemCompraRequest(itemSeleccionado.id());
            tiendaService.comprarItem(usuarioId, request);
            saldoUsuario -= itemSeleccionado.precio();

            System.out.println("[COMPRA]  Compra exitosa. Nuevo saldo: " + saldoUsuario);
            uiHelper.showInfo("Compra exitosa", "¡Has comprado " + itemSeleccionado.nombre() + "!");

        } catch (RuntimeException e) {
            System.out.println("[COMPRA]  Error de lógica: " + e.getMessage());
            uiHelper.showError("No se pudo completar la compra", e.getMessage());
        } catch (Exception e) {
            System.out.println("[COMPRA] ⚠ Error inesperado: " + e.getMessage());
            uiHelper.showError("Error inesperado al comprar", e.getMessage());
        }
    }

    // 🔁 Método llamado desde el popup
    public void forzarCompraDesdePopup(ItemDetalleResponse producto) {
        if (producto == null || itemSeleccionado == null) {
            System.err.println("[POPUP->TIENDA] No hay item seleccionado o producto nulo.");
            return;
        }
        onComprarItem(); // Reutiliza la lógica principal
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        try {
            Navigacion nav = new Navigacion();
            nav.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
        } catch (Exception e) {
            uiHelper.showError("Error al volver al inicio", e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML private void goForum() { uiHelper.showInfo("Foro", "Pantalla de foro aún no conectada."); }
    @FXML private void goProfile() { uiHelper.showInfo("Perfil", "Pantalla de perfil aún no implementada."); }
    @FXML private void goPomodoro() { uiHelper.showInfo("Pomodoro", "Desde tienda aún no se ha conectado."); }
    @FXML
    private void cerrarSesion() {
        try {
            // Cierra la sesión actual (borra el usuario en memoria)
            Application.config.AppServices.cerrarSesion();

            // 🔹 Redirige al login usando el mismo Navigacion que usas para las demás vistas
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);

            System.out.println("[NAV] Sesión cerrada correctamente. Redirigiendo al Login...");
        } catch (Exception e) {
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
            e.printStackTrace();
        }
    }
}


