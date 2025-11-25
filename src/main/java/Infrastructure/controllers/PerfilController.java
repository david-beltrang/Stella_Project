package Infrastructure.controllers;

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
import java.util.function.Function;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private VBox cursosContainer; // Container for dynamic course list
    @FXML
    private Label cursosCompletadosLabel; // Assuming there's a label for this, or we add one dynamically
    @FXML
    private Label fechaCreacionLabel; // Assuming there's a label for this
    @FXML
    private Button homeBtn2; // For navegation context
    @FXML
    private Button logoutBtn; // For navegation context

    private final PerfilService perfilService;
    private final Navegacion navegacion = new Navegacion();
    private Function<Class<?>, Object> controllerFactory;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        cargarDatosPerfil();
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

            // Populate courses
            if (cursosContainer != null) {
                cursosContainer.getChildren().clear();
                List<String> cursos = perfil.cursosInscritos();
                if (cursos != null && !cursos.isEmpty()) {
                    for (String cursoNombre : cursos) {
                        // Create a simple row for the course
                        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox();
                        row.setSpacing(10);
                        row.setStyle(
                                "-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 5; -fx-padding: 10;");

                        Label nameLabel = new Label(cursoNombre);
                        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
                        nameLabel.setPrefWidth(378);

                        Label progressLabel = new Label("En progreso"); // Placeholder
                        progressLabel.setStyle("-fx-text-fill: #4DA3FF; -fx-font-size: 14px;");

                        row.getChildren().addAll(nameLabel, progressLabel);
                        cursosContainer.getChildren().add(row);
                    }
                } else {
                    Label emptyLabel = new Label("No estás inscrito en ningún curso.");
                    emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                    cursosContainer.getChildren().add(emptyLabel);
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
    private void cerrarSesion() {
        // Logic to logout
        navegacion.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, logoutBtn);
    }
}
