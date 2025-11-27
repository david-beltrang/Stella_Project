package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.PreguntasRespuestasForoService;
import Application.services.UsuarioStatsService;
import Domain.models.PreguntaForo;
import Domain.models.RespuestaForo;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ResponderForoController {
    private static final Logger logger = LoggerFactory.getLogger(ResponderForoController.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    @FXML
    private Label preguntaTituloLabel;
    @FXML
    private Label preguntaContenidoLabel;
    @FXML
    private VBox respuestasContainer;
    @FXML
    private TextArea respuestaTextArea;
    @FXML
    private Button enviarBtn;
    @FXML
    private Button cancelarBtn;
    @FXML
    private Button volverBtn;

    // Stats Labels
    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;

    private final PreguntasRespuestasForoService foroService;
    private final UsuarioStatsService usuarioStatsService;
    private int preguntaId;

    public ResponderForoController(PreguntasRespuestasForoService foroService) {
        this.foroService = foroService;
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
    }

    public ResponderForoController() {
        this.foroService = AppServices.foroService();
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        preguntaId = AppServices.getPreguntaIdActual();
        cargarPregunta();
        cargarRespuestas();
        configurarBotones();
        cargarStats();
    }

    private void cargarStats() {
        if (usuarioStatsService == null)
            return;
        try {
            if (pescaditosLabel != null) {
                pescaditosLabel.setText(String.valueOf(usuarioStatsService.obtenerPescaditos()));
            }
            if (rachaLabel != null) {
                rachaLabel.setText(String.valueOf(usuarioStatsService.obtenerRachaDias()));
            }
        } catch (Exception e) {
            logger.error("Error cargando stats", e);
        }
    }

    private void configurarBotones() {
        if (enviarBtn != null) {
            enviarBtn.setOnAction(e -> enviarRespuesta());
        }
        if (cancelarBtn != null) {
            cancelarBtn.setOnAction(e -> limpiarFormulario());
        }
        if (volverBtn != null) {
            volverBtn.setOnAction(e -> volverAlForo());
        }
    }

    private void cargarPregunta() {
        try {
            Optional<PreguntaForo> preguntaOpt = foroService.obtenerPregunta(preguntaId);
            if (preguntaOpt.isPresent()) {
                PreguntaForo pregunta = preguntaOpt.get();
                if (preguntaTituloLabel != null) {
                    preguntaTituloLabel.setText(pregunta.getTitulo());
                }
                if (preguntaContenidoLabel != null) {
                    preguntaContenidoLabel.setText(pregunta.getContenido());
                }
            }
        } catch (Exception e) {
            logger.error("Error cargando pregunta", e);
        }
    }

    private void cargarRespuestas() {
        if (respuestasContainer == null)
            return;

        try {
            respuestasContainer.getChildren().clear();
            List<RespuestaForo> respuestas = foroService.listarRespuestas(preguntaId);

            if (respuestas.isEmpty()) {
                Label noRespuestas = new Label("Aún no hay respuestas. ¡Sé el primero en responder!");
                noRespuestas.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 14px;");
                respuestasContainer.getChildren().add(noRespuestas);
            } else {
                for (RespuestaForo respuesta : respuestas) {
                    VBox respuestaBox = crearRespuestaUI(respuesta);
                    respuestasContainer.getChildren().add(respuestaBox);
                }
            }
        } catch (Exception e) {
            logger.error("Error cargando respuestas", e);
        }
    }

    private VBox crearRespuestaUI(RespuestaForo respuesta) {
        VBox box = new VBox(8);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8; -fx-padding: 15;");

        Label contenido = new Label(respuesta.getContenido());
        contenido.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        contenido.setWrapText(true);

        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label usuario = new Label("Usuario " + respuesta.getUsuarioId());
        usuario.setStyle("-fx-text-fill: #4DA3FF; -fx-font-size: 12px;");

        Label fecha = new Label(respuesta.getFechaCreacion().format(formatter));
        fecha.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");

        infoBox.getChildren().addAll(usuario, fecha);
        box.getChildren().addAll(contenido, infoBox);

        return box;
    }

    private void enviarRespuesta() {
        String contenido = respuestaTextArea.getText();

        if (contenido == null || contenido.trim().isEmpty()) {
            uiHelper.showError("Error", "La respuesta no puede estar vacía");
            return;
        }

        try {
            var usuario = AppServices.getUsuarioActual();
            if (usuario == null) {
                uiHelper.showError("Error", "No hay sesión activa");
                return;
            }
            foroService.crearRespuesta(preguntaId, usuario.id(), contenido);
            uiHelper.showInfo("Éxito", "Respuesta publicada correctamente");
            limpiarFormulario();
            cargarRespuestas();
        } catch (Exception e) {
            logger.error("Error enviando respuesta", e);
            uiHelper.showError("Error", "No se pudo enviar la respuesta: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        if (respuestaTextArea != null) {
            respuestaTextArea.clear();
        }
    }

    @FXML
    private void volverAlForo() {
        try {
            navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, volverBtn);
        } catch (Exception e) {
            logger.error("Error volviendo al foro", e);
        }
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
    }

    @FXML
    private void goProfile() {
        navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, null);
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
    private void goGamificacion() {
        navigator.goTo("/views/Gamificacion.fxml", "STELLA - Gamificación", controllerFactory, null);
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
            popupStage.initOwner(respuestaTextArea.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            respuestaTextArea.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> respuestaTextArea.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo chatbot", e);
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            AppServices.cerrarSesion();
            navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error cerrando sesión", e);
        }
    }
}
