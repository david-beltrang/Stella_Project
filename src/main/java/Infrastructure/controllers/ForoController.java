package Infrastructure.controllers;

import Application.config.AppServices;
import Application.dtos.foro.PreguntaForoDTO;
import Application.services.PreguntasRespuestasForoService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Application.services.UsuarioStatsService;

import java.util.List;
import java.util.function.Function;

public class ForoController {
    private static final Logger logger = LoggerFactory.getLogger(ForoController.class);

    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    @FXML
    private Button homeBtn2;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button forumBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private Button tiendaBtn;
    @FXML
    private Button chatBtn1;
    @FXML
    private Label pescaditosLabel;
    @FXML
    private Label rachaLabel;

    // Campos del formulario
    @FXML
    private TextField tituloField;
    @FXML
    private TextArea contenidoArea;
    @FXML
    private Button publicarBtn;
    @FXML
    private VBox preguntasContainer;

    private final UsuarioStatsService usuarioStatsService;
    private final PreguntasRespuestasForoService foroService;

    public ForoController(UsuarioStatsService usuarioStatsService, PreguntasRespuestasForoService foroService) {
        this.usuarioStatsService = usuarioStatsService;
        this.foroService = foroService;
    }

    public ForoController() {
        this.usuarioStatsService = AppServices.getUsuarioStatsService();
        this.foroService = AppServices.foroService();
    }

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    @FXML
    private void initialize() {
        logger.debug("Iniciando ForoController...");
        cargarPescaditos();
        cargarRacha();
        cargarPreguntas();
        configurarFormulario();
        logger.debug("Foro inicializado correctamente");
    }

    private void cargarRacha() {
        if (rachaLabel == null) {
            return;
        }
        try {
            if (usuarioStatsService == null) {
                rachaLabel.setText("0");
                return;
            }
            int racha = usuarioStatsService.obtenerRachaDias();
            rachaLabel.setText(String.valueOf(racha));
            logger.debug("Racha cargada en Foro: {}", racha);
        } catch (Exception e) {
            logger.error("Error cargando racha", e);
            rachaLabel.setText("0");
        }
    }

    private void configurarFormulario() {
        if (publicarBtn != null) {
            publicarBtn.setOnAction(e -> publicarPregunta());
        }
    }

    private void cargarPreguntas() {
        if (preguntasContainer == null || foroService == null)
            return;

        try {
            preguntasContainer.getChildren().clear();
            List<PreguntaForoDTO> preguntas = foroService.listarTodasPreguntasConRespuestas();

            for (PreguntaForoDTO pregunta : preguntas) {
                VBox preguntaBox = crearPreguntaUI(pregunta);
                preguntasContainer.getChildren().add(preguntaBox);
            }
        } catch (Exception e) {
            logger.error("Error cargando preguntas", e);
        }
    }

    private VBox crearPreguntaUI(PreguntaForoDTO pregunta) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 10; -fx-padding: 15;");

        Label titulo = new Label(pregunta.titulo());
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label contenido = new Label(pregunta.contenido());
        contenido.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        contenido.setWrapText(true);

        Label info = new Label(pregunta.nombreUsuario() + " • " + pregunta.fechaCreacion() + " • "
                + pregunta.cantidadRespuestas() + " respuestas");
        info.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 12px;");

        Button responderBtn = new Button("Responder");
        responderBtn.setStyle("-fx-background-color: #4DA3FF; -fx-text-fill: white; -fx-background-radius: 5;");
        responderBtn.setOnAction(e -> irAResponder(pregunta.id()));

        box.getChildren().addAll(titulo, contenido, info, responderBtn);
        return box;
    }

    private void publicarPregunta() {
        String titulo = tituloField.getText();
        String contenido = contenidoArea.getText();

        if (titulo == null || titulo.trim().isEmpty()) {
            uiHelper.showError("Error", "El título es obligatorio");
            return;
        }
        if (contenido == null || contenido.trim().isEmpty()) {
            uiHelper.showError("Error", "El contenido es obligatorio");
            return;
        }

        try {
            var usuario = AppServices.getUsuarioActual();
            if (usuario == null) {
                uiHelper.showError("Error", "No hay sesión activa");
                return;
            }
            foroService.crearPregunta(usuario.id(), titulo, contenido);
            uiHelper.showInfo("Éxito", "Pregunta publicada correctamente");
            tituloField.clear();
            contenidoArea.clear();
            cargarPreguntas();
        } catch (Exception e) {
            logger.error("Error publicando pregunta", e);
            uiHelper.showError("Error", "No se pudo publicar la pregunta: " + e.getMessage());
        }
    }

    private void irAResponder(int preguntaId) {
        try {
            // Guardar el ID de la pregunta para usarlo en ResponderForoController
            AppServices.setPreguntaIdActual(preguntaId);
            navigator.goTo("/views/ResponderForo.fxml", "STELLA - Responder", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error navegando a responder", e);
            uiHelper.showError("Error", "No se pudo abrir la vista de respuestas");
        }
    }

    private void cargarPescaditos() {
        if (pescaditosLabel == null) {
            return;
        }
        try {
            if (usuarioStatsService == null) {
                pescaditosLabel.setText("0");
                return;
            }
            int pescaditos = usuarioStatsService.obtenerPescaditos();
            pescaditosLabel.setText(String.valueOf(pescaditos));
            logger.debug("Pescaditos cargados en Foro: {}", pescaditos);
        } catch (Exception e) {
            logger.error("Error cargando pescaditos", e);
            pescaditosLabel.setText("0");
        }
    }

    // ========= Navegación =========
    @FXML
    private void goHome() {
        try {
            navigator.goTo("/views/Principal.fxml", "STELLA - Principal", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al inicio desde foro", e);
            uiHelper.showError("Error al navegar al inicio", e.getMessage());
        }
    }

    @FXML
    private void goProfile() {
        try {
            navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar al perfil desde foro", e);
            uiHelper.showError("Error al navegar al perfil", e.getMessage());
        }
    }

    @FXML
    private void goTienda() {
        try {
            navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar a la tienda desde foro", e);
            uiHelper.showError("Error al navegar a la tienda", e.getMessage());
        }
    }

    @FXML
    private void goForum() {
        // Ya estamos en el foro, no hacer nada
        logger.debug("Ya estás en la vista del Foro");
    }

    @FXML
    private void goGamificacion() {
        try {
            navigator.goTo("/views/Gamificacion.fxml", "STELLA - Gamificación", controllerFactory, null);
        } catch (Exception e) {
            logger.error("Error al navegar a gamificación desde foro", e);
            uiHelper.showError("Error al navegar a gamificación", e.getMessage());
        }
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
            popupStage.initOwner(contenidoArea.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            contenidoArea.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> contenidoArea.setEffect(null));

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
            logger.info("Sesión cerrada correctamente desde foro");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión desde foro", e);
            uiHelper.showError("Error al cerrar sesión", e.getMessage());
        }
    }
}
