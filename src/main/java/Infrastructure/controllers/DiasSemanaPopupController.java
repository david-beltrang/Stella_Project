package Infrastructure.controllers;

import Application.services.SesionPomodoroService;
import Application.dtos.sesionEstudio.Pomodoro.SesionEstudioResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class DiasSemanaPopupController {



    @FXML
    private Label titleLabel;

    @FXML
    private GridPane daysGrid;

    private SesionPomodoroService sesionService;

    public void setSesionService(SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
    }


    public DiasSemanaPopupController() {

    }


    private int year;
    private int month;
    private int week;

    private final int usuarioId = 1;


    private final Image terrenoVacio =
            new Image(getClass().getResourceAsStream("/Image/General/cuboo.png"));

    private final Image terrenoIglu =
            new Image(getClass().getResourceAsStream("/Image/General/igluu.png"));


    public DiasSemanaPopupController(SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
    }


    public void init(int year, int month, int week) {
        this.year = year;
        this.month = month;
        this.week = week;

        drawDays();
    }

    // =========================
    // DIBUJA CADA DÍA CON BACK REAL
    // =========================
    private void drawDays() {

        if (sesionService == null) {
            System.out.println("ERROR: sesionService NO fue inyectado");
            return;
        }

        daysGrid.getChildren().clear();
        titleLabel.setText("Semana " + week + " - " + month + "/" + year);

        WeekFields wf = WeekFields.ISO;

        LocalDate startOfWeek = LocalDate
                .ofYearDay(year, 1)
                .with(wf.weekOfWeekBasedYear(), week)
                .with(wf.dayOfWeek(), 1);

        List<SesionEstudioResponse> sesionesSemana =
                sesionService.obtenerSesionesPorSemana(usuarioId, year, week);


        if (sesionesSemana.isEmpty()) {
            Label msg = new Label("❄️ Esta semana no tiene sesiones registradas");
            msg.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 20;" +
                            "-fx-font-weight: bold;"
            );
            daysGrid.add(msg, 0, 0);
            return;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE dd", new Locale("es"));

        for (int col = 0; col < 7; col++) {

            LocalDate diaActual = startOfWeek.plusDays(col);

            VBox dayBox = new VBox(10);

            Label dayLabel = new Label(diaActual.format(formatter));
            dayLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

            VBox igluBox = new VBox(6);
            igluBox.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.08);" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 10;"
            );

            long sesionesDelDia = sesionesSemana.stream()
                    .filter(s -> s.fechaInicio().toLocalDateTime().toLocalDate().equals(diaActual))
                    .count();


            for (int i = 0; i < sesionesDelDia && i < 7; i++) {
                igluBox.getChildren().add(crearImagenIglu());
            }


            for (int i = (int) sesionesDelDia; i < 7; i++) {
                igluBox.getChildren().add(crearImagenVacia());
            }

            dayBox.getChildren().addAll(dayLabel, igluBox);
            daysGrid.add(dayBox, col, 0);
        }
    }

    // =========================
    // CREACIÓN IMÁGENES
    // =========================
    private ImageView crearImagenVacia() {
        ImageView img = new ImageView(terrenoVacio);
        img.setFitWidth(60);
        img.setPreserveRatio(true);
        return img;
    }

    private ImageView crearImagenIglu() {
        ImageView img = new ImageView(terrenoIglu);
        img.setFitWidth(60);
        img.setPreserveRatio(true);
        return img;
    }

    // =========================
    // CERRAR POPUP
    // =========================
    @FXML
    private void cerrar() {
        Stage stage = (Stage) daysGrid.getScene().getWindow();
        stage.close();
    }
}
