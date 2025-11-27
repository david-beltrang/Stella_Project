package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.PerfilDTO;
import Application.services.PerfilService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import Infrastructure.ui.Navegacion;
import javafx.scene.control.Button;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class PerfilController {

    // ===== FXML =====
    @FXML private TextField nombreField;
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ImageView avatarImage;
    @FXML private VBox cursosContainer;
    @FXML private Label cursosCompletadosLabel;
    @FXML private Label fechaCreacionLabel;
    @FXML private Button homeBtn2;
    @FXML private Button logoutBtn;

    // ===== SERVICES =====
    private final PerfilService perfilService;
    private final Navegacion navegacion = new Navegacion();
    private Function<Class<?>, Object> controllerFactory;

    // ===== CONSTRUCTOR PARA INYECCIÓN (factory) =====
    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    // ===== CONSTRUCTOR PARA FXML (sin factory) =====
    public PerfilController() {
        this.perfilService = AppServices.getPerfilService();
    }

    // ===== SET FACTORY =====
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    // ===== INIT =====
    @FXML
    public void initialize() {
        cargarDatosPerfil();
    }

    // ===== LOAD USER DATA =====
    private void cargarDatosPerfil() {
        try {
            PerfilDTO perfil = perfilService.obtenerPerfilUsuarioActual();

            nombreField.setText(perfil.nombre());
            nicknameField.setText(perfil.nickname());
            emailField.setText(perfil.email());
            passwordField.setText(perfil.contrasena());

            // Imagen
            if (perfil.rutaImagenStella() != null && !perfil.rutaImagenStella().isEmpty()) {
                try {
                    avatarImage.setImage(new Image(getClass().getResourceAsStream(perfil.rutaImagenStella())));
                } catch (Exception e) {
                    System.err.println("Error loading avatar image: " + e.getMessage());
                }
            }

            // Cursos inscritos
            cursosContainer.getChildren().clear();
            List<String> cursos = perfil.cursosInscritos();

            if (cursos != null && !cursos.isEmpty()) {
                for (String cursoNombre : cursos) {
                    javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
                    row.setSpacing(10);
                    row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-border-radius: 8; -fx-padding: 10;");

                    Label nameLabel = new Label(cursoNombre);
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                    nameLabel.setPrefWidth(378);

                    Label progressLabel = new Label("En progreso");
                    progressLabel.setStyle("-fx-text-fill: #4DA3FF; -fx-font-size: 14px;");

                    row.getChildren().addAll(nameLabel, progressLabel);
                    cursosContainer.getChildren().add(row);
                }
            } else {
                Label emptyLabel = new Label("No estás inscrito en ningún curso.");
                emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                cursosContainer.getChildren().add(emptyLabel);
            }

            // Fecha creación
            if (fechaCreacionLabel != null && perfil.fechaCreacion() != null) {
                fechaCreacionLabel.setText(
                        "Te uniste a Stella en: " +
                                perfil.fechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }

            // Cursos completados
            if (cursosCompletadosLabel != null) {
                cursosCompletadosLabel.setText(
                        "Cursos completados: " + perfil.cantidadCursosCompletados()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== NAVIGATION =====
    @FXML private void goHome() {
        navegacion.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, homeBtn2);
    }

    @FXML private void goForum() {
        navegacion.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, null);
    }

    @FXML private void goTienda() {
        navegacion.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
    }

    @FXML private void goIglu() {
        navegacion.goTo("/views/Iglu.fxml", "STELLA - Iglú", controllerFactory, null);
    }

    @FXML
    private void cerrarSesion() {
        AppServices.cerrarSesion();
        AppServices.init(new Application.services.DarAcceso.LoginService(
                new Infrastructure.repositories.UsuarioRepository(
                        Infrastructure.persistence.ConexionBD.getInstance()
                )
        ));
        navegacion.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, logoutBtn);
    }
}
