package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.Prueba.IntentoRequest;
import Application.dtos.Prueba.PruebaResponse;
import Application.dtos.Prueba.RespuestaRequest;
import Application.services.PruebaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador de la pantalla QuizLeccion.fxml.
 * Evalúa todas las preguntas visibles del quiz de una sección.
 */
public class QuizController {

    // ===== Dependencias =====
    private final PruebaService pruebaService;
    private final Application.services.UsuarioStatsService usuarioStatsService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navegacion navigator = new Navegacion();
    private Function<Class<?>, Object> controllerFactory;
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);

    // ===== FXML =====
    @FXML
    private Accordion quizAccordion;
    @FXML
    private Button finalizarQuiz;

    // Pregunta 1
    @FXML
    private Label pregunta1Label;
    @FXML
    private RadioButton Opcion1_1, Opcion1_2, Opcion1_3, Opcion1_4;
    @FXML
    private ToggleGroup pregunta1Group;

    // Pregunta 2
    @FXML
    private Label pregunta2Label;
    @FXML
    private RadioButton Opcion2_1, Opcion2_2, Opcion2_3, Opcion2_4;
    @FXML
    private ToggleGroup pregunta2Group;

    // Pregunta 3
    @FXML
    private Label pregunta3Label;
    @FXML
    private RadioButton Opcion3_1, Opcion3_2, Opcion3_3, Opcion3_4;
    @FXML
    private ToggleGroup pregunta3Group;

    // ===== Estado =====
    private PruebaResponse quizActual;
    private final List<RespuestaRequest> respuestasUsuario = new ArrayList<>();

    // ===== Constructores =====
    public QuizController(PruebaService pruebaService, Application.services.UsuarioStatsService usuarioStatsService) {
        this.pruebaService = pruebaService;
        this.usuarioStatsService = usuarioStatsService;
    }

    public QuizController() {
        this.pruebaService = null;
        this.usuarioStatsService = null;
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
            logger.info("Cargando quiz para sección: {}", seccionId);
            quizActual = pruebaService.obtenerQuizPorSeccion(seccionId);
            if (quizActual == null || quizActual.preguntas().isEmpty()) {
                logger.warn("Quiz no encontrado o sin preguntas para sección {}", seccionId);
                uiHelper.showError("Error", "No hay preguntas registradas para esta sección.");
                return;
            }

            logger.info("Quiz cargado: {} con {} preguntas", quizActual.titulo(), quizActual.preguntas().size());

            // Asignar los textos de las preguntas y opciones
            configurarPregunta(quizActual, 0, pregunta1Label, Opcion1_1, Opcion1_2, Opcion1_3, Opcion1_4);
            configurarPregunta(quizActual, 1, pregunta2Label, Opcion2_1, Opcion2_2, Opcion2_3, Opcion2_4);
            configurarPregunta(quizActual, 2, pregunta3Label, Opcion3_1, Opcion3_2, Opcion3_3, Opcion3_4);

        } catch (Exception e) {
            logger.error("Error cargando quiz", e);
            uiHelper.showError("Error cargando quiz", e.getMessage());
        }
    }

    private void configurarPregunta(PruebaResponse quiz, int index, Label labelPregunta, RadioButton a, RadioButton b,
            RadioButton c,
            RadioButton d) {
        if (index >= quiz.preguntas().size()) {
            logger.debug("Índice {} fuera de rango para preguntas (size: {})", index, quiz.preguntas().size());
            return;
        }
        var pregunta = quiz.preguntas().get(index);

        // Set question text
        if (labelPregunta != null) {
            String texto = (index + 1) + ". " + pregunta.enunciado();
            logger.info("Configurando pregunta {}: {}", index + 1, texto);
            labelPregunta.setText(texto);
        } else {
            logger.error("labelPregunta es NULL para índice {}", index);
        }

        var opciones = pregunta.opciones();

        if (opciones.size() >= 4) {
            a.setText(opciones.get(0).texto());
            b.setText(opciones.get(1).texto());
            c.setText(opciones.get(2).texto());
            d.setText(opciones.get(3).texto());
        } else {
            logger.warn("Pregunta {} tiene menos de 4 opciones ({})", index, opciones.size());
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
                    respuestasUsuario);

            var resultado = pruebaService.crearIntento(intento);

            // Otorgar 100 pescaditos por cada respuesta correcta
            int pescaditosGanados = resultado.aciertos() * 100;
            if (pescaditosGanados > 0 && usuarioStatsService != null) {
                try {
                    usuarioStatsService.agregarPescaditos(AppServices.getUsuarioActual().id(), pescaditosGanados);
                } catch (Exception ex) {
                    System.err.println("Error al agregar pescaditos: " + ex.getMessage());
                }
            }

            uiHelper.showInfo("Resultado del Quiz",
                    "Puntaje: " + String.format("%.1f", resultado.puntaje()) + " / 100\n" +
                            "Aciertos: " + resultado.aciertos() + " de " + resultado.totalPreguntas() + "\n" +
                            "Pescaditos ganados: +" + pescaditosGanados + " 🐟\n" +
                            resultado.mensaje());

            // Regresar a la pantalla del curso
            navigator.goTo("/views/PlantillaCurso.fxml", "Volver al curso", controllerFactory, finalizarQuiz);

        } catch (Exception e) {
            uiHelper.showError("Error al finalizar quiz", e.getMessage());
        }
    }

    private void agregarRespuesta(int index, ToggleGroup grupo,
            RadioButton a, RadioButton b, RadioButton c, RadioButton d) {
        if (index >= quizActual.preguntas().size())
            return;

        var pregunta = quizActual.preguntas().get(index);
        var seleccion = (RadioButton) grupo.getSelectedToggle();

        if (seleccion == null)
            return;

        var texto = seleccion.getText();
        int opcionId = pregunta.opciones().stream()
                .filter(o -> o.texto().equals(texto))
                .findFirst()
                .map(o -> o.id())
                .orElse(0);

        respuestasUsuario.add(new RespuestaRequest(pregunta.id(), opcionId));
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
    private void goTienda() {
        navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
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
        uiHelper.showInfo("Pomodoro", "Desde tienda aún no se ha conectado.");
    }
}