package Infrastructure.controllers;

import Application.dtos.leccion.LeccionResponse;
import Application.dtos.seccion.SeccionResponse;
import Application.services.SeccionesService;
import Application.services.LeccionService;
import Application.services.PruebaService;
import Infrastructure.ui.AyudaUI;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.function.Function;

import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CursoController {
    private static final Logger logger = LoggerFactory.getLogger(CursoController.class);

    // ===== DEPENDENCIAS =====
    private final SeccionesService seccionesService;
    private final LeccionService leccionService;
    private final PruebaService pruebaService;
    private final Navegacion navigator = new Navegacion();
    private final AyudaUI uiHelper = new AyudaUI();
    private Function<Class<?>, Object> controllerFactory;

    // ===== FXML =====
    @FXML
    private Pane root;
    @FXML
    private Label NombreCurso;

    // ===== DATOS =====
    private int cursoActualId = -1;
    private String cursoTitulo = "CURSO DESCONOCIDO";

    // ===== CONSTRUCTOR =====
    public CursoController(SeccionesService seccionesService, LeccionService leccionService,
            PruebaService pruebaService) {
        this.seccionesService = seccionesService;
        this.leccionService = leccionService;
        this.pruebaService = pruebaService;
    }

    public CursoController() {
        this.seccionesService = null;
        this.leccionService = null;
        this.pruebaService = null;
    }

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    // ===== CONFIGURAR CURSO =====
    public void setCursoActual(int id, String titulo) {
        this.cursoActualId = id;
        this.cursoTitulo = titulo;

        if (root != null) {
            cargarSeccionesYLecciones();
        }
    }

    @FXML
    public void initialize() {
        if (NombreCurso != null) {
            NombreCurso.setText(cursoTitulo.toUpperCase());
        }
        if (seccionesService != null && cursoActualId > 0) {
            cargarSeccionesYLecciones();
        }
    }

    // ===== CARGAR SECCIONES Y LECCIONES =====
    private void cargarSeccionesYLecciones() {
        try {
            if (root == null) {
                logger.warn("root es null");
                return;
            }
            if (seccionesService == null) {
                logger.warn("seccionesService es null");
                return;
            }
            if (cursoActualId <= 0) {
                logger.warn("cursoActualId no seteado");
                return;
            }

            List<SeccionResponse> secciones = seccionesService.ListarSeccionesConLecciones(cursoActualId);
            logger.debug("Secciones encontradas: {}", secciones.size());

            for (SeccionResponse seccion : secciones) {
                for (LeccionResponse leccion : seccion.lecciones()) {

                    // Buscar botón por convención (Leccion1.1 o Leccion1_1)
                    String idPunto = "Leccion" + seccion.numeroOrden() + "." + leccion.numeroOrden();
                    String idGuion = "Leccion" + seccion.numeroOrden() + "_" + leccion.numeroOrden();

                    Button btn = (Button) root.lookup("#" + idPunto);
                    if (btn == null)
                        btn = (Button) root.lookup("#" + idGuion);

                    if (btn == null) {
                        logger.debug("No se encontró el botón #{} ni #{}", idPunto, idGuion);
                        continue;
                    }

                    btn.setText(leccion.titulo());
                    btn.setDisable(false);
                    btn.setOpacity(1.0);

                    // capturar el número de sección
                    final int seccionOrden = seccion.numeroOrden();
                    final LeccionResponse lec = leccion;

                    btn.setOnAction(e -> abrirLeccion(lec, seccionOrden, (Node) e.getSource()));
                }
            }

            // Configurar botones de quiz dinámicamente
            for (SeccionResponse seccion : secciones) {
                // Asumimos que el botón del quiz es el 5to elemento (LeccionX_5)
                // O buscamos un botón específico para el quiz si existiera una convención
                // distinta
                String quizBtnId = "Leccion" + seccion.numeroOrden() + "_5";
                Button quizBtn = (Button) root.lookup("#" + quizBtnId);

                if (quizBtn != null) {
                    try {
                        var quiz = pruebaService.obtenerQuizPorSeccion(seccion.id());
                        if (quiz != null) {
                            quizBtn.setText("📝 " + quiz.titulo());
                            quizBtn.setDisable(false);
                            quizBtn.setOpacity(1.0);
                            // Estilo dinámico según la sección (opcional, se puede mantener o generalizar)
                            String colorStyle = switch (seccion.numeroOrden()) {
                                case 1 -> "-fx-background-color: #4CAF50;";
                                case 2 -> "-fx-background-color: #2196F3;";
                                case 3 -> "-fx-background-color: #FF9800;";
                                default -> "-fx-background-color: #9C27B0;";
                            };
                            quizBtn.setStyle("-fx-font-weight: bold; " + colorStyle + " -fx-text-fill: white;");

                            final int seccionId = seccion.id();
                            quizBtn.setOnAction(e -> abrirQuiz(seccionId, (Node) e.getSource()));
                        } else {
                            quizBtn.setText("Quiz no disponible");
                            quizBtn.setDisable(true);
                        }
                    } catch (Exception e) {
                        logger.error("Error cargando quiz para sección {}", seccion.id(), e);
                        quizBtn.setText("Error cargar Quiz");
                    }
                }
            }

            if (NombreCurso != null) {
                NombreCurso.setText(cursoTitulo.toUpperCase());
            }

        } catch (Exception e) {
            logger.error("Error al cargar curso", e);
            uiHelper.showError("Error al cargar curso", e.getMessage());
        }
    }

    // ===== ABRIR LECCIÓN =====
    private void abrirLeccion(LeccionResponse leccion, int seccionOrden, Node source) {
        try {
            int numero = (leccion != null) ? leccion.numeroOrden() : 1;
            // 🔹 CORREGIDO: Usar plantilla genérica en lugar de archivos específicos
            String ruta = "/views/LeccionPlantilla.fxml";

            // Obtener contenido real desde BD
            LeccionResponse dto = leccionService.obtenerLeccionPorCursoYOrden(
                    cursoActualId,
                    seccionOrden,
                    numero);

            navigator.goToWithInit(
                    ruta,
                    "Lección " + numero,
                    controllerFactory,
                    (source != null ? source : root),
                    (LeccionController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.setNumeroActual(numero);
                        c.setCursoYSeccion(cursoActualId, seccionOrden);
                        c.setLeccionActual(dto);
                    });

        } catch (Exception e) {
            logger.error("Error al abrir lección", e);
            uiHelper.showError("Error al abrir lección", e.getMessage());
        }
    }

    // ===== ABRIR QUIZ =====
    private void abrirQuiz(int seccionId, Node source) {
        try {
            navigator.goToWithInit(
                    "/views/QuizPlantilla.fxml",
                    "Quiz Final",
                    controllerFactory,
                    (source != null ? source : root),
                    (QuizController c) -> {
                        c.setControllerFactory(controllerFactory);
                        c.cargarQuiz(seccionId);
                    });
        } catch (Exception e) {
            logger.error("Error al abrir quiz", e);
            uiHelper.showError("Error al abrir quiz", e.getMessage());
        }
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
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);
            popupStage.centerOnScreen();

            // Efecto blur en el fondo
            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            logger.error("Error abriendo chatbot", e);
        }
    }
}