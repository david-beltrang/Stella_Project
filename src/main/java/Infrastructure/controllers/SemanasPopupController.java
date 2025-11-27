package Infrastructure.controllers;

import Application.services.SesionPomodoroService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.LinkedHashSet;
import java.util.Set;

public class SemanasPopupController {

    @FXML
    private GridPane weeksGrid;

    @FXML
    private Label titleLabel;

    private int year;
    private int month;

    private SesionPomodoroService sesionService;

    public void setSesionService(SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
        System.out.println("sesionService heredado en SemanasPopupController");
    }

    public void init(int year, int month) {
        this.year = year;
        this.month = month;

        if (sesionService == null) {
            System.out.println("ERROR: sesionService NO fue heredado en SemanasPopupController");
        }

        drawWeeks();
    }

    private void drawWeeks() {
        weeksGrid.getChildren().clear();
        titleLabel.setText("Semanas de " + month + "/" + year);

        WeekFields wf = WeekFields.ISO;
        Set<Integer> semanas = new LinkedHashSet<>();

        LocalDate date = LocalDate.of(year, month, 1);
        LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

        while (!date.isAfter(end)) {
            semanas.add(date.get(wf.weekOfWeekBasedYear()));
            date = date.plusDays(1);
        }

        int row = 0;
        int col = 0;

        for (int semana : semanas) {
            Button btn = new Button("Semana " + semana);
            btn.setPrefSize(180, 70);
            btn.getStyleClass().add("popup-btn");

            btn.setOnAction(e -> abrirDiasSemana(semana));

            weeksGrid.add(btn, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void abrirDiasSemana(int semana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DiasSemanaPopup.fxml"));
            Parent rootPopup = loader.load();

            DiasSemanaPopupController controller = loader.getController();

            if (this.sesionService == null) {
                System.out.println("ERROR: sesionService es NULL antes de pasar a DiasSemanaPopupController");
            } else {
                controller.setSesionService(this.sesionService);
            }

            controller.init(year, month, semana);

            Scene scene = new Scene(rootPopup);
            scene.getStylesheets().add(getClass().getResource("/styles/LoginStyle.css").toExternalForm());

            Stage stage = new Stage(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(weeksGrid.getScene().getWindow());
            stage.setScene(scene);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) weeksGrid.getScene().getWindow();
        stage.close();
    }
}
