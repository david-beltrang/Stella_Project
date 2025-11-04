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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Controlador de la vista de evaluación (quiz) de cada sección.
 * Integra el servicio real PruebaService y gestiona la interacción con el usuario.
 */
public class PruebaController {

    // ===== Dependencias =====
    private final PruebaService pruebaService;
    private final AyudaUI uiHelper = new AyudaUI();
    private final Navigacion navigator = new Navigacion();
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML private Label tituloLabel;
    @FXML private Label preguntaLabel;
    @FXML private RadioButton opcionA;
    @FXML private RadioButton opcionB;
    @FXML private RadioButton opcionC;
    @FXML private RadioButton opcionD;
    @FXML private Button verificarBtn;
    @FXML private Button siguienteBtn;
    @FXML private Label resultadoLabel;

    private final ToggleGroup grupoOpciones = new ToggleGroup();

    // ===== Estado interno =====
    private PruebaResponse quizActual;
    private int indicePregunta = 0;
    private final List<RespuestaRequest> respuestasUsuario = new ArrayList<>();

    // ===== Constructor =====
    public PruebaController(PruebaService pruebaService) {
        this.pruebaService = pruebaService;
    }

    public PruebaController() {
        this.pruebaService = null; // se inyecta desde ControllerControladores
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== Inicialización =====
    @FXML
    public void initialize() {
        opcionA.setToggleGroup(grupoOpciones);
        opcionB.setToggleGroup(grupoOpciones);
        opcionC.setToggleGroup(grupoOpciones);
        opcionD.setToggleGroup(grupoOpciones);
    }

    /**
     * Este método lo debe llamar el controlador anterior (por ejemplo PrincipalController)
     * para cargar el quiz de una sección específica.
     */
    public void cargarQuiz(int seccionId) {
        try {
            quizActual = pruebaService.obtenerQuizPorSeccion(seccionId);
            tituloLabel.setText(quizActual.titulo());
            indicePregunta = 0;
            mostrarPreguntaActual();
        } catch (Exception e) {
            uiHelper.showError("Error cargando prueba", e.getMessage());
        }
    }

    private void mostrarPreguntaActual() {
        if (quizActual == null || quizActual.preguntas().isEmpty()) {
            uiHelper.showError("Error", "No hay preguntas disponibles para esta prueba.");
            return;
        }

        if (indicePregunta >= quizActual.preguntas().size()) {
            finalizarPrueba();
            return;
        }

        var pregunta = quizActual.preguntas().get(indicePregunta);
        preguntaLabel.setText((indicePregunta + 1) + ". " + pregunta.enunciado());
        resultadoLabel.setText("");

        var opciones = pregunta.opciones();
        if (opciones.size() >= 4) {
            opcionA.setText(opciones.get(0).texto());
            opcionB.setText(opciones.get(1).texto());
            opcionC.setText(opciones.get(2).texto());
            opcionD.setText(opciones.get(3).texto());
        } else {
            opcionA.setText(opciones.get(0).texto());
            opcionB.setText(opciones.size() > 1 ? opciones.get(1).texto() : "");
            opcionC.setText("");
            opcionD.setText("");
        }

        grupoOpciones.selectToggle(null);
    }

    @FXML
    private void verificarRespuesta() {
        RadioButton seleccionada = (RadioButton) grupoOpciones.getSelectedToggle();
        if (seleccionada == null) {
            uiHelper.showInfo("Aviso", "Selecciona una opción antes de verificar.");
            return;
        }

        var pregunta = quizActual.preguntas().get(indicePregunta);
        var textoSeleccionado = seleccionada.getText();

        // Buscar ID de la opción elegida según texto
        int opcionSeleccionadaId = pregunta.opciones().stream()
                .filter(o -> o.texto().equals(textoSeleccionado))
                .findFirst()
                .map(o -> o.id())
                .orElse(0);

        // Guardar respuesta temporalmente
        respuestasUsuario.add(new RespuestaRequest(pregunta.id(), opcionSeleccionadaId));

        // Feedback visual (sin saber todavía si es correcta)
        resultadoLabel.setText("Respuesta registrada ✔");
        resultadoLabel.setStyle("-fx-text-fill: white;");
    }

    @FXML
    private void siguientePregunta() {
        indicePregunta++;
        mostrarPreguntaActual();
    }

    @FXML
    private void finalizarPrueba() {
        try {
            if (respuestasUsuario.isEmpty()) {
                uiHelper.showInfo("Aviso", "No se han respondido preguntas.");
                return;
            }

            var intento = new IntentoRequest(
                    AppServices.getUsuarioActual().id(),
                    quizActual.id(),
                    respuestasUsuario
            );

            var resultado = pruebaService.crearIntento(intento);
            uiHelper.showInfo("Resultado",
                    "Tu puntaje: " + String.format("%.1f", resultado.puntaje()) + " / 100\n" +
                            "Aciertos: " + resultado.aciertos() + " de " + resultado.totalPreguntas() + "\n" +
                            resultado.mensaje());
        } catch (Exception e) {
            uiHelper.showError("Error al finalizar prueba", e.getMessage());
        }
    }
}