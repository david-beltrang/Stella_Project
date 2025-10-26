package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.Listado_Cursos.CursoResponse;
import Application.dtos.Listado_Cursos.CursosResponse;
import Application.dtos.Listado_Cursos.InscripcionRequest;
import Application.services.ListarCursosService;
import Infrastructure.persistence.ConexionBD;
import Infrastructure.persistence.H2DataBaseInitializer;
import Infrastructure.persistence.IConexionBD;
import Infrastructure.repositories.CursoRepository;
import Infrastructure.repositories.UsuarioCursoRepository;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ScrollPane misCursosScroll;
    @FXML private ScrollPane cursosDisponiblesScroll;
    @FXML private HBox misCursosContainer;
    @FXML private HBox cursosDisponiblesContainer;
    @FXML private Label noCoursesLabel;
    @FXML private Button leftArrow, rightArrow;

    private ListarCursosService listarCursosService;
    private int usuarioActualId = AppServices.getUsuarioActual().id();
    private CursosResponse cursosActuales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Instancia del servicio los repos de curso
        IConexionBD connMgr = new ConexionBD();
        H2DataBaseInitializer initializer = new H2DataBaseInitializer(connMgr);
        initializer.initialize();
        listarCursosService = new ListarCursosService(
                new CursoRepository(connMgr),
                new UsuarioCursoRepository(connMgr)
        );

        cargarCursosDesdeBD();
        configurarFlechas();
    }

    //Se cargan los cursos desde la base de datos
    private void cargarCursosDesdeBD() {
        try {
            cursosActuales = listarCursosService.obtenerCursosCompletos(usuarioActualId);
            cargarMisCursos();
            cargarCursosDisponibles();
        } catch (Exception e) {
            mostrarError("Error cargando cursos", e.getMessage());
        }
    }

    //se revisa cuales son los cursos que ya tiene el usuario y se cargan visualmente
    private void cargarMisCursos() {
        misCursosContainer.getChildren().clear();

        if (cursosActuales.cursosUsuario().isEmpty()) {
            noCoursesLabel.setVisible(true);
            return;
        }

        noCoursesLabel.setVisible(false);

        for (CursoResponse curso : cursosActuales.cursosUsuario()) {
            VBox card = crearTarjetaCurso(curso);
            misCursosContainer.getChildren().add(card);
        }
    }

    //se crean los cuadros de los cursos
    private VBox crearTarjetaCurso(CursoResponse curso) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.6); -fx-background-radius: 15; -fx-padding: 15;");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        Label nivel = new Label("Nivel: " + curso.nivel());
        nivel.setStyle("-fx-text-fill: #B0C4DE; -fx-font-size: 14;");

        Button btn = new Button("Continuar");
        btn.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnAction(e -> mostrarInfo("Curso: " + curso.titulo(), "Abrir contenido próximamente."));

        card.getChildren().addAll(title, nivel, btn);
        return card;
    }

    //Se revisan que cursos no tiene el usuario es decir los disponiblea s a añadir y los carga visualmente
    private void cargarCursosDisponibles() {
        cursosDisponiblesContainer.getChildren().clear();

        for (CursoResponse curso : cursosActuales.cursosDisponibles()) {
            VBox card = crearTarjetaCursoDisponible(curso);
            cursosDisponiblesContainer.getChildren().add(card);
        }
    }

    private VBox crearTarjetaCursoDisponible(CursoResponse curso) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.65); -fx-background-radius: 20; -fx-padding: 15;");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        Button agregar = new Button("Agregar");
        agregar.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white;");
        agregar.setOnAction(e -> inscribirCurso(curso.id()));

        card.getChildren().addAll(title, agregar);
        return card;
    }

    //Se contruye una solicitud es decir un DTO y se llama al metodo de servicio para incribir el curso
    private void inscribirCurso(int cursoId) {
        try {
            InscripcionRequest request = new InscripcionRequest(usuarioActualId, cursoId);
            cursosActuales = listarCursosService.inscribirCurso(request);
            cargarMisCursos();
            cargarCursosDisponibles();
            mostrarInfo("Inscripción exitosa", "El curso fue agregado correctamente.");
        } catch (Exception e) {
            mostrarError("Error al inscribir curso", e.getMessage());
        }
    }

    //Lo siguiente son configuraciones y ajuestes visuales para la estetica y funcionamiento visual de la pantalla

    private void configurarFlechas() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setOnAction(e -> scrollLeft());
            rightArrow.setOnAction(e -> scrollRight());
        }
    }

    private void scrollLeft() { scrollHorizontally(cursosDisponiblesScroll, -0.3); }
    private void scrollRight() { scrollHorizontally(cursosDisponiblesScroll, 0.3); }

    private void scrollHorizontally(ScrollPane scrollPane, double delta) {
        double newValue = scrollPane.getHvalue() + delta;
        newValue = Math.max(0, Math.min(1, newValue));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.hvalueProperty(), newValue))
        );
        timeline.play();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}
