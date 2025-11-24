// Application/services/CursoUIService.java
package Application.services;

import Application.dtos.Listado_Cursos.CursoResponse;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Servicio para la creación de componentes UI relacionados con cursos.
 * Aplica Separation of Concerns: extrae la lógica de creación de UI de los controladores.
 * Aplica Information Expert: este servicio es experto en crear componentes UI de cursos.
 */
public class CursoUIService {

    private static final String STYLE_CARD_MIS_CURSOS = "-fx-background-color: rgba(8,7,54,0.6); -fx-background-radius: 15; -fx-padding: 15;";
    private static final String STYLE_CARD_DISPONIBLES = "-fx-background-color: rgba(8,7,54,0.65); -fx-background-radius: 20; -fx-padding: 15;";
    private static final String STYLE_TITLE = "-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;";
    private static final String STYLE_NIVEL = "-fx-text-fill: #B0C4DE; -fx-font-size: 14;";
    private static final String STYLE_BTN_CONTINUAR = "-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final String STYLE_BTN_AGREGAR = "-fx-background-color: #00BFA6; -fx-text-fill: white;";
    private static final double CARD_WIDTH = 300.0;
    private static final double CARD_HEIGHT = 200.0;
    private static final double CARD_SPACING = 10.0;

    /**
     * Crea una tarjeta UI para un curso del usuario (mis cursos).
     * Aplica Factory Method para crear componentes UI.
     *
     * @param curso Datos del curso
     * @param onContinuar Acción a ejecutar al hacer clic en "Continuar"
     * @return VBox con la tarjeta del curso
     */
    public VBox crearTarjetaCursoUsuario(CursoResponse curso, Consumer<CursoResponse> onContinuar) {
        VBox card = new VBox(CARD_SPACING);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle(STYLE_CARD_MIS_CURSOS);
        card.setAlignment(Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle(STYLE_TITLE);

        Label nivel = new Label("Nivel: " + curso.nivel());
        nivel.setStyle(STYLE_NIVEL);

        Button btn = new Button("Continuar");
        btn.setStyle(STYLE_BTN_CONTINUAR);
        btn.setOnAction(e -> onContinuar.accept(curso));

        card.getChildren().addAll(title, nivel, btn);
        return card;
    }

    /**
     * Crea una tarjeta UI para un curso disponible.
     * Aplica Factory Method para crear componentes UI.
     *
     * @param curso Datos del curso
     * @param onAgregar Acción a ejecutar al hacer clic en "Agregar"
     * @return VBox con la tarjeta del curso
     */
    public VBox crearTarjetaCursoDisponible(CursoResponse curso, Consumer<CursoResponse> onAgregar) {
        VBox card = new VBox(CARD_SPACING);
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setStyle(STYLE_CARD_DISPONIBLES);
        card.setAlignment(Pos.CENTER);

        Label title = new Label(curso.titulo());
        title.setStyle(STYLE_TITLE);

        Button agregar = new Button("Agregar");
        agregar.setStyle(STYLE_BTN_AGREGAR);
        agregar.setOnAction(e -> onAgregar.accept(curso));

        card.getChildren().addAll(title, agregar);
        return card;
    }
}

