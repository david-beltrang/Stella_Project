package Infrastructure.controllers;

import Application.dtos.leccion.LeccionLista;
import Domain.models.UsuarioValueObjects.UsuarioId;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.function.Function;

/**
 * Controlador de la pantalla de LECCIÓN.
 * - Recibe el contexto (usuario, curso) y la lección seleccionada (LeccionLista).
 * - Muestra título, tipo y estado.
 * - Opcionalmente permite volver a curso o sección.
 */
public class LeccionController {

    @FXML private Label lblTitulo;
    @FXML private Label lblTipo;
    @FXML private Label lblEstado;
    @FXML private Button btnVolverCurso;
    @FXML private Button btnVolverSeccion;

    private UsuarioId usuarioId;
    private LeccionLista leccion;

    // Factory global para navegar (inyectada por ControllerControladores)
    private Function<Class<?>, Object> controllerFactory;

    /** Inyección de la factory global desde ControllerControladores */
    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    /** Método llamado por quien abre esta pantalla (le pasa el contexto). */
    public void setContext(UsuarioId usuarioId, LeccionLista leccion) {
        this.usuarioId = usuarioId;
        this.leccion = leccion;
        pintar();
    }

    /** Muestra la información de la lección en la vista */
    private void pintar() {
        if (leccion == null) {
            new Alert(Alert.AlertType.ERROR, "Lección no recibida").showAndWait();
            return;
        }
        lblTitulo.setText(leccion.titulo());
        lblTipo.setText("Tipo: " + leccion.tipoContenido());
        lblEstado.setText("Estado: " + leccion.estado().name());
    }

    // ----- Navegación opcional -----

    @FXML
    private void volverAlCurso() {
        cambiarVista("/views/Curso.fxml", "STELLA - Curso", btnVolverCurso);
    }

    @FXML
    private void volverALaSeccion() {
        cambiarVista("/views/Seccion.fxml", "STELLA - Sección", btnVolverSeccion);
    }

    /** Carga una nueva vista usando la factory global */
    private void cambiarVista(String fxmlPath, String titulo, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }
            Parent next = loader.load();

            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(new Scene(next));
            stage.setTitle(titulo);
            stage.centerOnScreen();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo cambiar la vista: " + e.getMessage()).showAndWait();
        }
    }
}
