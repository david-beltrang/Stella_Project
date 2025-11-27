package Infrastructure.controllers;

import Application.dtos.PerfilDTO;
import Application.services.PerfilService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import Infrastructure.ui.Navegacion;
import javafx.scene.control.Button;
import java.util.function.Function;
import Application.services.UsuarioStatsService;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

public class PerfilController {

    @FXML
    private TextField nombreField;
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ImageView avatarImage;
    @FXML
    private VBox cursosContainer;
    @FXML
    private Label cursosCompletadosLabel;
    @FXML
    private Label fechaCreacionLabel;
    @FXML
    private Button homeBtn2;
    @FXML
    private Button logoutBtn;

    private final PerfilService perfilService;
    private final UsuarioStatsService usuarioStatsService;
    private final Navegacion navegacion = new Navegacion();
    private Function<Class<?>, Object> controllerFactory;
    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;

    public PerfilController(PerfilService perfilService, UsuarioStatsService usuarioStatsService) {
        this.perfilService = perfilService;
        this.usuarioStatsService = usuarioStatsService;
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        cargarDatosPerfil();
        cargarPescaditos();
        cargarRacha();
    }

    private void cargarPescaditos() {
        if (pescaditosLabel == null) {
            return;
        }
        try {
            int pescaditos = usuarioStatsService.obtenerPescaditos();
            pescaditosLabel.setText(String.valueOf(pescaditos));
            logger.debug("Pescaditos cargados en Perfil: {}", pescaditos);
        } catch (Exception e) {
            logger.error("Error cargando pescaditos", e);
            pescaditosLabel.setText("0");
        }
    }

    private void cargarRacha() {
        if (rachaLabel == null) {
            return;
        }
        try {
            int racha = usuarioStatsService.obtenerRachaDias();
            rachaLabel.setText(String.valueOf(racha));
            logger.debug("Racha cargada en Perfil: {}", racha);
        } catch (Exception e) {
            logger.error("Error cargando racha", e);
            rachaLabel.setText("0");
        }
    }

    private void cargarDatosPerfil() {
        try {
            PerfilDTO perfil = perfilService.obtenerPerfilUsuarioActual();

            nombreField.setText(perfil.nombre());
            nicknameField.setText(perfil.nickname());
            emailField.setText(perfil.email());
            passwordField.setText(perfil.contrasena());

            if (perfil.rutaImagenStella() != null && !perfil.rutaImagenStella().isEmpty()) {
                try {
                    avatarImage.setImage(new Image(getClass().getResourceAsStream(perfil.rutaImagenStella())));
                } catch (Exception e) {
                    System.err.println("Error loading avatar image: " + e.getMessage());
                }
            }

            // Display Creation Date
            if (fechaCreacionLabel != null && perfil.fechaCreacion() != null) {
                fechaCreacionLabel.setText("Te uniste a Stella en: "
                        + perfil.fechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            // Display Completed Courses
            if (cursosCompletadosLabel != null) {
                cursosCompletadosLabel.setText("Cursos completados: " + perfil.cantidadCursosCompletados());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error (show alert)
        }
    }

    @FXML
    private void goHome() {
        navegacion.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, homeBtn2);
    }

    @FXML
    private void goForum() {
        navegacion.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, null);
    }

    @FXML
    private void goTienda() {
        navegacion.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
    }

    @FXML
    private void goInventario() {
        navegacion.goTo("/views/InventarioAvatar.fxml", "STELLA - Mi Inventario", controllerFactory, null);
    }

    @FXML
    private void goProfile() {
        // Ya estamos en perfil, no hacer nada o mostrar mensaje
        logger.debug("Ya estás en la vista de Perfil");
    }

    @FXML
    private void goIglu() {
        navegacion.goTo("/views/Iglu.fxml", "STELLA - Iglu", controllerFactory, null);
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
            popupStage.initOwner(avatarImage.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            avatarImage.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> avatarImage.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo chatbot", e);
        }
    }

    @FXML
    private void cerrarSesion() {
        // Logic to logout
        navegacion.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, logoutBtn);
    }
}
