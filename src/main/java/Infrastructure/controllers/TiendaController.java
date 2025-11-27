package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.tienda.*;
import Application.services.TiendaService;
import Application.services.UsuarioStatsService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import Infrastructure.ui.ImageLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    private final UsuarioStatsService usuarioStatsService;
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private final ImageLoader imageLoader = new ImageLoader();
    private Function<Class<?>, Object> controllerFactory;

    private int usuarioId = 1;
    private int saldoUsuario = 0;

    @FXML
    private AnchorPane root;

    @FXML
    private ScrollPane itemsScrollPane;

    @FXML
    private VBox itemsContainer;

    @FXML
    private ImageView imgItem;

    @FXML
    private Label pescaditosLabel;

    @FXML
    private Label rachaLabel;

    private List<ItemTiendaResponse> items = new ArrayList<>();
    private ItemTiendaResponse itemSeleccionado;

    public TiendaController(TiendaService tiendaService, UsuarioStatsService usuarioStatsService) {
        this.tiendaService = tiendaService;
        this.usuarioStatsService = usuarioStatsService;
    }

    public TiendaController() {
        this.tiendaService = AppServices.getTiendaService();
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
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
        cargarItemsEnUI();
        cargarPescaditos();
        cargarRacha();

        imgItem.setImage(null);
        logger.debug("Tienda inicializada correctamente");
    }

    /**
     * Carga y muestra la cantidad de pescaditos del usuario en el label
     */
    private void cargarPescaditos() {
        if (pescaditosLabel == null) {
            logger.warn("pescaditosLabel es NULL");
            return;
        }
        try {
            pescaditosLabel.setText(String.valueOf(saldoUsuario));
            logger.debug("Pescaditos cargados en label: {}", saldoUsuario);
        } catch (Exception e) {
            logger.error("Error al cargar pescaditos en label", e);
            pescaditosLabel.setText("0");
        }
    }

    // New method to load racha
    private void cargarRacha() {
        if (rachaLabel == null) {
            logger.warn("rachaLabel es NULL");
            return;
        }
        try {
            int racha = usuarioStatsService.obtenerRachaDias();
            rachaLabel.setText(String.valueOf(racha));
            logger.debug("Racha cargada en Tienda: {}", racha);
        } catch (Exception e) {
            logger.error("Error al cargar racha", e);
            rachaLabel.setText("0");
        }
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

    /**
     * Carga todos los items en el contenedor VBox dinámicamente
     * Organiza los items en filas de 3 columnas
     */
    private void cargarItemsEnUI() {
        if (itemsContainer == null) {
            logger.error("itemsContainer es NULL");
            return;
        }

        itemsContainer.getChildren().clear();

        if (items == null || items.isEmpty()) {
            logger.warn("No hay ítems para mostrar");
            Label emptyLabel = new Label("No hay items disponibles");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
            itemsContainer.getChildren().add(emptyLabel);
            return;
        }

        // Organizar items en filas de 3
        HBox currentRow = null;
        int itemsPerRow = 3;

        for (int i = 0; i < items.size(); i++) {
            // Crear nueva fila cada 3 items
            if (i % itemsPerRow == 0) {
                currentRow = new HBox(15);
                currentRow.setAlignment(Pos.CENTER);
                currentRow.setStyle("-fx-padding: 5;");
                itemsContainer.getChildren().add(currentRow);
            }

            ItemTiendaResponse item = items.get(i);
            VBox card = crearTarjetaItem(item);
            currentRow.getChildren().add(card);
        }

        logger.info("Se cargaron {} tarjetas de items en la UI organizadas en filas de {}", items.size(), itemsPerRow);
    }

    /**
     * Crea una tarjeta visual para un item
     * Patrón: Similar a crearTarjetaCursoUsuario en PrincipalController
     */
    private VBox crearTarjetaItem(ItemTiendaResponse item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);");
        card.setPrefWidth(230);
        card.setMaxWidth(230);
        card.setMinWidth(230);

        // Imagen del item
        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);
        imageLoader.cargarImagen(imageView, item.imagePath());

        // Nombre del item
        Label nameLabel = new Label(item.nombre());
        nameLabel.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(210);
        nameLabel.setAlignment(Pos.CENTER);

        card.getChildren().addAll(imageView, nameLabel);

        // Click handler - abre el popup de detalles
        card.setOnMouseClicked(e -> mostrarPopupProducto(item));

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.2);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(77,163,255,0.6), 15, 0, 0, 3);"));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1);" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 15;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"));

        return card;
    }

    private void mostrarItem(ItemTiendaResponse item) {
        if (item == null)
            return;

        itemSeleccionado = item;
        imageLoader.cargarImagen(imgItem, item.imagePath());
        logger.debug("Item seleccionado: {}", item.nombre());
    }

    /**
     * Abre el popup con los detalles del producto
     */
    private void mostrarPopupProducto(ItemTiendaResponse item) {
        if (item == null) {
            logger.warn("Intento de mostrar popup con item nulo");
            return;
        }

        itemSeleccionado = item;
        logger.info("Abriendo popup para item: {}", item.nombre());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ProductoTienda.fxml"));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent popupRoot = loader.load();

            ProductoTiendaController controller = loader.getController();
            if (controller != null) {
                ItemDetalleRequest request = new ItemDetalleRequest(item.id());
                ItemDetalleResponse detalle = tiendaService.obtenerDetalleItem(request);
                controller.setProducto(detalle);
            }

            Scene popupScene = new Scene(popupRoot, 1100, 750);
            popupScene.setFill(Color.TRANSPARENT);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Store TiendaController reference in stage properties for purchase callback
            popupStage.getProperties().put("controller", this);

            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo popup de producto", e);
            uiHelper.showError("Error", "No se pudo abrir el detalle del producto: " + e.getMessage());
        }
    }

    @FXML
    private void onComprarItem() {
        if (itemSeleccionado == null) {
            uiHelper.showInfo("Selecciona un item", "Primero debes seleccionar un item para comprar.");
            return;
        }

        try {
            ItemCompraRequest request = new ItemCompraRequest(itemSeleccionado.id());
            tiendaService.comprarItem(usuarioId, request);

            // Actualizar saldo después de compra exitosa
            saldoUsuario = tiendaService.obtenerSaldoUsuario(usuarioId);
            cargarPescaditos(); // Update UI
            uiHelper.showInfo("Compra exitosa", "Has comprado " + itemSeleccionado.nombre() + " exitosamente!");
            logger.info("Compra exitosa: {}", itemSeleccionado.nombre());

        } catch (Exception e) {
            logger.error("Error durante la compra", e);
            uiHelper.showError("Error", e.getMessage());
        }
    }

    public void forzarCompraDesdePopup(ItemDetalleResponse producto) {
        if (producto == null || itemSeleccionado == null) {
            logger.warn("No hay item seleccionado o producto nulo al intentar comprar desde popup");
            return;
        }
        onComprarItem();
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
    private void goIglu() {
        navigator.goTo("/views/Iglu.fxml", "STELLA - Iglu", controllerFactory, null);
    }

    @FXML
    private void goChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Chatbot.fxml"));
            if (controllerFactory != null)
                loader.setControllerFactory(controllerFactory::apply);

            Parent popupRoot = loader.load();
            Scene popupScene = new Scene(popupRoot, 1100, 750);
            popupScene.setFill(Color.TRANSPARENT);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo chatbot", e);
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            Application.config.AppServices.cerrarSesion();
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);
            logger.info("Sesión cerrada correctamente desde tienda");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión desde tienda", e);
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
        }
    }
}
