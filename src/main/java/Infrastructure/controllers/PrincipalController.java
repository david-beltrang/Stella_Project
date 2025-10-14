package Infrastructure.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class PrincipalController implements Initializable {

    // --- Botones de navegación ---
    @FXML private Button pomodoroButton;
    @FXML private Button volverButton;

    // --- Elementos nuevos ---
    @FXML private TextField searchField;
    @FXML private ScrollPane misCursosScroll;
    @FXML private ScrollPane cursosDisponiblesScroll;
    @FXML private HBox misCursosContainer;
    @FXML private HBox cursosDisponiblesContainer;
    @FXML private Label noCoursesLabel;
    @FXML private Button leftArrow, rightArrow;

    // --- Datos simulados ---
    private final List<Map<String, Object>> mockCursosDisponibles = List.of(
            Map.of("nombre", "Java Avanzado"),
            Map.of("nombre", "Python para Principiantes"),
            Map.of("nombre", "C++ Estructuras de Datos"),
            Map.of("nombre", "JavaScript Moderno"),
            Map.of("nombre", "HTML y CSS Pro")
    );

    private final List<Map<String, Object>> misCursos = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (cursosDisponiblesContainer != null) {
            cargarCursosDisponibles();
        }
        configurarFlechas();
    }

    // ========================
    //  CURSOS DISPONIBLES
    // ========================
    private void cargarCursosDisponibles() {
        cursosDisponiblesContainer.getChildren().clear();

        for (Map<String, Object> curso : mockCursosDisponibles) {
            String nombre = (String) curso.get("nombre");
            boolean yaExiste = misCursos.stream().anyMatch(c -> c.get("nombre").equals(nombre));
            if (!yaExiste) {
                VBox card = crearTarjetaCursoDisponible(nombre);
                cursosDisponiblesContainer.getChildren().add(card);
            }
        }
    }

    private VBox crearTarjetaCursoDisponible(String nombre) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.65); -fx-background-radius: 20; -fx-padding: 15;");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label(nombre);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        Button verDetalles = new Button("Ver Detalles");
        verDetalles.setStyle("-fx-background-color: #3366FF; -fx-text-fill: white;");

        Button agregar = new Button("Agregar");
        agregar.setStyle("-fx-background-color: #00BFA6; -fx-text-fill: white;");
        agregar.setOnAction(e -> agregarAMisCursos(nombre));

        HBox botones = new HBox(10, verDetalles, agregar);
        botones.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(title, botones);
        return card;
    }

    private void agregarAMisCursos(String nombre) {
        boolean yaExiste = misCursos.stream().anyMatch(c -> c.get("nombre").equals(nombre));
        if (yaExiste) return;

        Map<String, Object> nuevoCurso = new HashMap<>();
        nuevoCurso.put("nombre", nombre);
        nuevoCurso.put("progreso", 0);

        misCursos.add(nuevoCurso);

        // Actualiza ambas secciones
        cargarMisCursos();
        cargarCursosDisponibles();

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Curso agregado");
        alerta.setHeaderText(null);
        alerta.setContentText("El curso \"" + nombre + "\" se agregó correctamente a Mis Cursos.");
        alerta.showAndWait();
    }

    // ========================
    //  MIS CURSOS
    // ========================
    private void cargarMisCursos() {
        misCursosContainer.getChildren().clear();

        if (misCursos.isEmpty()) {
            noCoursesLabel.setVisible(true);
            return;
        } else {
            noCoursesLabel.setVisible(false);
        }

        for (Map<String, Object> curso : misCursos) {
            VBox card = crearTarjetaCurso((String) curso.get("nombre"), (int) curso.get("progreso"));
            misCursosContainer.getChildren().add(card);
        }
    }

    private VBox crearTarjetaCurso(String nombre, int progreso) {
        VBox card = new VBox(10);
        card.setPrefSize(300, 200);
        card.setStyle("-fx-background-color: rgba(8,7,54,0.6); -fx-background-radius: 15; -fx-padding: 15;");
        card.setAlignment(javafx.geometry.Pos.CENTER);

        Label title = new Label(nombre);
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");

        ProgressBar bar = new ProgressBar(progreso / 100.0);
        bar.setPrefWidth(250);
        bar.setStyle("-fx-accent: #4A90E2;");

        Label percent = new Label(progreso + "% completado");
        percent.setStyle("-fx-text-fill: white;");

        Button btn = new Button("Continuar");
        btn.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold;");

        btn.setOnAction(e -> {
            if (nombre.equalsIgnoreCase("C++ Estructuras de Datos")) {
                goToPomodoro(); // Abre Pomodoro.fxml
            } else {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Curso en desarrollo");
                alerta.setHeaderText("Curso no disponible aún");
                alerta.setContentText("Este curso está en proceso de habilitarse. Inténtalo más adelante.");
                alerta.showAndWait();
            }
        });

        card.getChildren().addAll(title, bar, percent, btn);
        return card;
    }

    // ========================
    //  SCROLL ANIMADO
    // ========================
    private void configurarFlechas() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setOnAction(e -> scrollLeft());
            rightArrow.setOnAction(e -> scrollRight());
        }
    }

    @FXML
    private void scrollLeft() {
        scrollHorizontally(cursosDisponiblesScroll, -0.3);
    }

    @FXML
    private void scrollRight() {
        scrollHorizontally(cursosDisponiblesScroll, 0.3);
    }

    private void scrollHorizontally(ScrollPane scrollPane, double delta) {
        double newValue = scrollPane.getHvalue() + delta;
        newValue = Math.max(0, Math.min(1, newValue));
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(400), new KeyValue(scrollPane.hvalueProperty(), newValue))
        );
        timeline.play();
    }

    // ========================
    //  BÚSQUEDA DE CURSO
    // ========================
    @FXML
    private void buscarCurso() {
        if (searchField == null) return;
        String query = searchField.getText().toLowerCase();
        List<Map<String, Object>> filtrados = new ArrayList<>();

        for (Map<String, Object> c : mockCursosDisponibles) {
            if (((String) c.get("nombre")).toLowerCase().contains(query)) {
                filtrados.add(c);
            }
        }

        cursosDisponiblesContainer.getChildren().clear();
        for (Map<String, Object> curso : filtrados) {
            String nombre = (String) curso.get("nombre");
            boolean yaExiste = misCursos.stream().anyMatch(c -> c.get("nombre").equals(nombre));
            if (!yaExiste) {
                VBox card = crearTarjetaCursoDisponible(nombre);
                cursosDisponiblesContainer.getChildren().add(card);
            }
        }

        if (filtrados.isEmpty()) {
            Label none = new Label("No se encontraron cursos con ese nombre.");
            none.setStyle("-fx-text-fill: white; -fx-font-size: 18;");
            cursosDisponiblesContainer.getChildren().add(none);
        }
    }

    // ========================
    //  MÉTODOS ORIGINALES
    // ========================
    @FXML
    private void goToPomodoro() {
        cambiarEscena("/views/Pomodoro.fxml", pomodoroButton);
    }

    @FXML
    private void goBackToLogin() {
        cambiarEscena("/views/Login.fxml", volverButton);
    }

    private void cambiarEscena(String fxmlPath, Button botonReferencia) {
        try {
            Parent vista = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(fxmlPath))
            );
            Stage stage = (Stage) botonReferencia.getScene().getWindow();
            stage.setScene(new Scene(vista));
            stage.centerOnScreen();
        } catch (Exception e) {
            Throwable root = e;
            while (root.getCause() != null) root = root.getCause();
            e.printStackTrace();

            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("No pude cargar la vista");
            a.setContentText(
                    fxmlPath + "\n\nCausa: " + root.getClass().getSimpleName() +
                            "\nMensaje: " + (root.getMessage() == null ? "(sin mensaje)" : root.getMessage())
            );
            a.showAndWait();
        }
    }

    @FXML private void goHome() {}
    @FXML private void goForum() {
        new Alert(Alert.AlertType.INFORMATION, "Pantalla de Foro aún no implementada.").showAndWait();
    }
    @FXML private void goAchievements() {
        new Alert(Alert.AlertType.INFORMATION, "Pantalla de Logros aún no implementada.").showAndWait();
    }
    @FXML private void goProfile() {
        new Alert(Alert.AlertType.INFORMATION, "Pantalla de Perfil aún no implementada.").showAndWait();
    }
}



