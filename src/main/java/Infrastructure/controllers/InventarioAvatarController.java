package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.tienda.ItemTiendaResponse;
import Application.services.TiendaService;
import Application.services.UsuarioStatsService;
import Application.services.UsuarioStellaService;
import Infrastructure.repositories.UsuarioItemRepository;
import Infrastructure.repositories.StellaItemRepository;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class InventarioAvatarController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioAvatarController.class);

    private final TiendaService tiendaService;
    private final UsuarioStatsService usuarioStatsService;
    private final UsuarioStellaService usuarioStellaService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navegacion navigator = new Navegacion();
    private Function<Class<?>, Object> controllerFactory;

    private int usuarioId = 1;
    private int saldoUsuario = 0;

    @FXML
    private GridPane itemsGrid;
    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;
    @FXML
    private Button backButton;
    @FXML
    private AnchorPane root;

    public InventarioAvatarController(TiendaService tiendaService, UsuarioStatsService usuarioStatsService) {
        this.tiendaService = tiendaService;
        this.usuarioStatsService = usuarioStatsService;
        this.usuarioStellaService = new UsuarioStellaService(
                new UsuarioItemRepository(ConexionBD.getInstance()),
                new StellaItemRepository(ConexionBD.getInstance()));
    }

    public InventarioAvatarController() {
        this.tiendaService = AppServices.getTiendaService();
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
        this.usuarioStellaService = AppServices.getUsuarioStellaService();
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {

        try {
            saldoUsuario = tiendaService.obtenerSaldoUsuario(usuarioId);
            logger.info("Saldo cargado correctamente: {} pescaditos", saldoUsuario);
        } catch (Exception e) {
            saldoUsuario = 0;
            logger.error("Error obteniendo saldo del usuario {}", usuarioId, e);
        }

        cargarPescaditos();
        cargarInventario();
        cargarRacha();
    }

    private void cargarPescaditos() {
        if (pescaditosLabel != null && usuarioStatsService != null) {
            try {
                int pescaditos = usuarioStatsService.obtenerPescaditos();
                pescaditosLabel.setText(String.valueOf(pescaditos));
            } catch (Exception e) {
                logger.error("Error cargando pescaditos", e);
                pescaditosLabel.setText("0");
            }
        }
    }

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

    private void cargarInventario() {
        if (itemsGrid == null || tiendaService == null)
            return;

        itemsGrid.getChildren().clear();
        Integer usuarioId = usuarioStatsService.obtenerUsuarioIdActual();

        if (usuarioId == null) {
            uiHelper.showError("Error", "No hay usuario activo.");
            return;
        }

        try {
            List<ItemTiendaResponse> items = tiendaService.obtenerInventario(usuarioId);
            int col = 0;
            int row = 0;

            for (ItemTiendaResponse item : items) {
                VBox card = crearTarjetaInventario(item);
                itemsGrid.add(card, col, row);
                col++;
                if (col == 3) { // 3 columnas
                    col = 0;
                    row++;
                }
            }
        } catch (Exception e) {
            logger.error("Error cargando inventario", e);
            uiHelper.showError("Error", "No se pudo cargar el inventario.");
        }
    }

    private VBox crearTarjetaInventario(ItemTiendaResponse item) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.getStyleClass().add("tienda-item-card"); // Reutilizamos estilo de tienda
        card.setPrefWidth(230);
        card.setMaxWidth(230);
        card.setMinWidth(230);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);
        // Aquí deberíamos usar un ImageLoader, pero por simplicidad asumimos que la
        // ruta es válida o usamos placeholder
        // Si tienes ImageLoader inyectado, úsalo. Si no, usa new Image.
        // Por ahora usaré una lógica simple similar a TiendaController si tuviera
        // ImageLoader
        // Como no tengo ImageLoader aquí, usaré un placeholder o intentaré cargar si es
        // recurso
        try {
            String imagePath = item.imagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                imageView.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(imagePath)));
            }
        } catch (Exception e) {
            // Fallback
        }

        Label nameLabel = new Label(item.nombre());
        nameLabel.getStyleClass().add("tienda-item-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(210);
        nameLabel.setAlignment(Pos.CENTER);

        Button equiparBtn = new Button("Utilizar");
        equiparBtn.setStyle(
                "-fx-background-color: #4DA3FF; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        equiparBtn.setOnAction(e -> equiparItem(item));

        card.getChildren().addAll(imageView, nameLabel, equiparBtn);
        return card;
    }

    private void equiparItem(ItemTiendaResponse item) {
        try {
            // Cambiar el avatar usando UsuarioStellaService
            usuarioStellaService.cambiarStellaActual(item.id());

            uiHelper.showInfo("Avatar cambiado", "Has equipado: " + item.nombre());
            logger.info("Avatar cambiado exitosamente al item {}: {}", item.id(), item.nombre());

        } catch (Exception e) {
            logger.error("Error equipando item", e);
            uiHelper.showError("Error", "No se pudo equipar el item: " + e.getMessage());
        }
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
    private void goGamificacion() {
        try {
            navigator.goTo("/views/Pomodoro.fxml", "STELLA - Pomodoro", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar a pomodoro desde inventario", e);
            uiHelper.showError("Error al navegar a pomodoro", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar a tienda desde inventario", e);
            uiHelper.showError("Error al navegar a tienda", e.getMessage());
        }
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
