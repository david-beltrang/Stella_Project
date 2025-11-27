package Infrastructure.controllers;

import Application.config.AppServices;
import Application.services.SesionPomodoroService;
import Infrastructure.ui.Navegacion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.util.function.Function;

public class IgluController {

    private SesionPomodoroService sesionService;
    private Function<Class<?>, Object> controllerFactory;
    private final Navegacion navigator = new Navegacion();


    public IgluController(SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
    }


    public IgluController() {}

    public void setControllerFactory(Function<Class<?>, Object> factory) {
        this.controllerFactory = factory;
    }

    public void setSesionService(SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
    }

    // =========================
    // FXML
    // =========================
    @FXML private AnchorPane root;
    @FXML private GridPane monthGrid;
    @FXML private Button yearLabelBtn;

    // =========================
    // ESTADO
    // =========================
    private int currentYear = LocalDate.now().getYear();

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {
        drawMonths();
    }

    // =========================
    // DIBUJO DE MESES
    // =========================
    private void drawMonths() {
        monthGrid.getChildren().clear();
        yearLabelBtn.setText(String.valueOf(currentYear));

        String[] months = {
                "Enero", "Febrero", "Marzo",
                "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre",
                "Octubre", "Noviembre", "Diciembre"
        };

        int row = 0, col = 0;

        for (int i = 0; i < 12; i++) {
            Button monthBtn = new Button(months[i]);
            monthBtn.setPrefSize(240, 100);
            monthBtn.getStyleClass().add("course-card");

            int selectedMonth = i + 1;
            monthBtn.setOnAction(e -> openWeeksPopup(selectedMonth));

            monthGrid.add(monthBtn, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    // =========================
    // CAMBIO DE AÑO
    // =========================
    @FXML
    private void nextYear() {
        currentYear++;
        drawMonths();
    }

    @FXML
    private void prevYear() {
        currentYear--;
        drawMonths();
    }

    // =========================
    // POPUP DE SEMANAS
    // =========================
    private void openWeeksPopup(int month) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SemanasPopup.fxml"));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent rootPopup = loader.load();
            SemanasPopupController controller = loader.getController();

            controller.setSesionService(this.sesionService);

            controller.init(currentYear, month);

            Scene scene = new Scene(rootPopup);
            scene.setFill(Color.TRANSPARENT);

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(root.getScene().getWindow());
            stage.setScene(scene);

            root.setEffect(new GaussianBlur(10));
            stage.setOnHidden(e -> root.setEffect(null));

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // CHATBOT
    // =========================
    @FXML
    private void goChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Chatbot.fxml"));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent popupRoot = loader.load();
            Scene popupScene = new Scene(popupRoot, 1100, 750);
            popupScene.setFill(Color.TRANSPARENT);

            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(root.getScene().getWindow());
            popupStage.setScene(popupScene);

            root.setEffect(new GaussianBlur(10));
            popupStage.setOnHidden(e -> root.setEffect(null));

            popupStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // NAVEGACIÓN INFERIOR
    // =========================
    @FXML
    private void goHome() {
        navigator.goTo("/views/Principal.fxml", "STELLA - Inicio", controllerFactory, root);
    }

    @FXML
    private void goForum() {
        navigator.goTo("/views/Foro.fxml", "STELLA - Foro", controllerFactory, root);
    }

    @FXML
    private void goProfile() {
        navigator.goTo("/views/Perfil.fxml", "STELLA - Perfil", controllerFactory, root);
    }

    @FXML
    private void goTienda() {
        navigator.goTo("/views/Tienda.fxml", "STELLA - Tienda", controllerFactory, root);
    }
    @FXML private void goIglu() {
        navigator.goTo("/views/Iglu.fxml", "STELLA - Iglú", controllerFactory, null);
    }


    @FXML
    private void cerrarSesion() {

        AppServices.cerrarSesion();
        AppServices.init(new Application.services.DarAcceso.LoginService(
                new Infrastructure.repositories.UsuarioRepository(
                        Infrastructure.persistence.ConexionBD.getInstance()
                )
        ));
        navigator.goTo("/views/Login.fxml", "STELLA - Login", controllerFactory, root);
    }
}
