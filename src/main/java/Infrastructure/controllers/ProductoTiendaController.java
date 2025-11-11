package Infrastructure.controllers;

import Application.dtos.tienda.ItemDetalleResponse;
import Application.services.TiendaService;
import Infrastructure.ui.AyudaUI;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.function.Function;

public class ProductoTiendaController {

    // ======== Dependencias ========
    private final TiendaService tiendaService;
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ======== FXML ========
    @FXML private ImageView imgProducto;
    @FXML private Label lblNombre;
    @FXML private Label lblDescripcion;
    @FXML private Label lblPrecio;
    @FXML private Button btnComprar;
    @FXML private Button btnCancelar;

    // ======== Estado ========
    private ItemDetalleResponse producto;

    // ======== Constructor (inyectado desde ControllerControladores) ========
    public ProductoTiendaController(TiendaService tiendaService) {
        this.tiendaService = tiendaService;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    @FXML
    private AnchorPane popupRoot; // Asegúrate de tenerlo vinculado

    @FXML
    private void initialize() {
        btnCancelar.setOnAction(e -> cerrar());
        btnComprar.setOnAction(e -> onComprar());

        // ✨ animación de entrada suave
        popupRoot.setOpacity(0);
        popupRoot.setScaleX(0.9);
        popupRoot.setScaleY(0.9);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), popupRoot);
        fade.setFromValue(0);
        fade.setToValue(1);

        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(javafx.util.Duration.millis(200), popupRoot);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);

        new javafx.animation.ParallelTransition(fade, scale).play();
    }



    public void setProducto(ItemDetalleResponse producto) {
        this.producto = producto;
        lblNombre.setText(producto.nombre());
        lblDescripcion.setText(producto.descripcion());
        lblPrecio.setText("$" + producto.precio());

        if (producto.stellaImagePath() != null) {
            URL url = getClass().getResource(producto.stellaImagePath());
            if (url != null) {
                imgProducto.setImage(new Image(url.toExternalForm()));
            } else {
                System.err.println("[ProductoTienda] Imagen no encontrada: " + producto.stellaImagePath());
            }
        }
    }

    // ======== Eventos ========
    @FXML
    private void onComprar() {
        try {
            System.out.println("[POPUP] Intentando comprar desde popup: " + producto.nombre());

            Stage stageActual = (Stage) btnComprar.getScene().getWindow();
            TiendaController tiendaCtrl = (TiendaController) stageActual.getProperties().get("controller");

            if (tiendaCtrl != null) {
                tiendaCtrl.forzarCompraDesdePopup(producto);
            } else {
                System.err.println("[POPUP] No se encontró el controlador de tienda ❌");
            }

            cerrar();

        } catch (Exception e) {
            uiHelper.showError("Error al comprar", e.getMessage());
        }
    }


    private void cerrar() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), popupRoot);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        });
        fadeOut.play();
    }

}
