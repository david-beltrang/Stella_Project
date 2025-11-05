package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.Prueba.IntentoRequest;
import Application.dtos.Prueba.PruebaResponse;
import Application.dtos.Prueba.RespuestaRequest;
import Application.services.PruebaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navigacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.function.Function;

/**
 * Controlador de la pantalla QuizLeccion.fxml.
 * Evalúa todas las preguntas visibles del quiz de una sección.
 */
public class QuizController {

    // ===== Dependencias =====
    private final PruebaService pruebaService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navigacion navigator = new Navigacion();
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Accordion quizAccordion;
    @FXML private Button finalizarQuiz;

    // Pregunta 1
    @FXML private RadioButton Opcion1_1, Opcion1_2, Opcion1_3, Opcion1_4;
    @FXML private ToggleGroup pregunta1Group;

    // Pregunta 2
    @FXML private RadioButton Opcion2_1, Opcion2_2, Opcion2_3, Opcion2_4;
    @FXML private ToggleGroup pregunta2Group;

    // Pregunta 3
    @FXML private RadioButton Opcion3_1, Opcion3_2, Opcion3_3, Opcion3_4;
    @FXML private ToggleGroup pregunta3Group;

    // ===== Estado =====
    private PruebaResponse quizActual;
    private final List<RespuestaRequest> respuestasUsuario = new ArrayList<>();

    // ===== Constructores =====
    public QuizController(PruebaService pruebaService) {
        this.pruebaService = pruebaService;
    }

    public QuizController() {
        this.pruebaService = null; // inyectado desde ControllerControladores
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== Inicialización =====
    @FXML
    public void initialize() {
        if (finalizarQuiz != null)
            finalizarQuiz.setOnAction(e -> finalizarQuiz());
    }

    // ===== Carga del quiz =====
    public void cargarQuiz(int seccionId) {
        try {
            quizActual = pruebaService.obtenerQuizPorSeccion(seccionId);
            if (quizActual == null || quizActual.preguntas().isEmpty()) {
                uiHelper.showError("Error", "No hay preguntas registradas para esta sección.");
                return;
            }

            // Asignar los textos de las preguntas y opciones
            configurarPregunta(quizActual, 0, Opcion1_1, Opcion1_2, Opcion1_3, Opcion1_4);
            configurarPregunta(quizActual, 1, Opcion2_1, Opcion2_2, Opcion2_3, Opcion2_4);
            configurarPregunta(quizActual, 2, Opcion3_1, Opcion3_2, Opcion3_3, Opcion3_4);

        } catch (Exception e) {
            uiHelper.showError("Error cargando quiz", e.getMessage());
        }
    }

    private void configurarPregunta(PruebaResponse quiz, int index, RadioButton a, RadioButton b, RadioButton c, RadioButton d) {
        if (index >= quiz.preguntas().size()) return;
        var pregunta = quiz.preguntas().get(index);
        var opciones = pregunta.opciones();

        if (opciones.size() >= 4) {
            a.setText(opciones.get(0).texto());
            b.setText(opciones.get(1).texto());
            c.setText(opciones.get(2).texto());
            d.setText(opciones.get(3).texto());
        }
    }

    // ===== Recolectar y enviar respuestas =====
    @FXML
    private void finalizarQuiz() {
        try {
            if (quizActual == null) {
                uiHelper.showError("Error", "No se ha cargado ningún quiz.");
                return;
            }

            respuestasUsuario.clear();

            // Recolectar todas las respuestas
            agregarRespuesta(0, pregunta1Group, Opcion1_1, Opcion1_2, Opcion1_3, Opcion1_4);
            agregarRespuesta(1, pregunta2Group, Opcion2_1, Opcion2_2, Opcion2_3, Opcion2_4);
            agregarRespuesta(2, pregunta3Group, Opcion3_1, Opcion3_2, Opcion3_3, Opcion3_4);

            if (respuestasUsuario.isEmpty()) {
                uiHelper.showInfo("Aviso", "No seleccionaste ninguna respuesta.");
                return;
            }

            // Crear intento y enviarlo al backend
            var intento = new IntentoRequest(
                    AppServices.getUsuarioActual().id(),
                    quizActual.id(),
                    respuestasUsuario
            );

            var resultado = pruebaService.crearIntento(intento);

            uiHelper.showInfo("Resultado del Quiz",
                    "Puntaje: " + String.format("%.1f", resultado.puntaje()) + " / 100\n" +
                            "Aciertos: " + resultado.aciertos() + " de " + resultado.totalPreguntas() + "\n" +
                            resultado.mensaje());

            // Regresar a la pantalla del curso
            navigator.goTo("/views/PlantillaCurso.fxml", "Volver al curso", controllerFactory, finalizarQuiz);

        } catch (Exception e) {
            uiHelper.showError("Error al finalizar quiz", e.getMessage());
        }
    }

    private void agregarRespuesta(int index, ToggleGroup grupo,
                                  RadioButton a, RadioButton b, RadioButton c, RadioButton d) {
        if (index >= quizActual.preguntas().size()) return;

        var pregunta = quizActual.preguntas().get(index);
        var seleccion = (RadioButton) grupo.getSelectedToggle();

        if (seleccion == null) return;

        var texto = seleccion.getText();
        int opcionId = pregunta.opciones().stream()
                .filter(o -> o.texto().equals(texto))
                .findFirst()
                .map(o -> o.id())
                .orElse(0);

        respuestasUsuario.add(new RespuestaRequest(pregunta.id(), opcionId));
    }
}
