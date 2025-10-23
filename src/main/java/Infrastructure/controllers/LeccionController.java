package Infrastructure.controllers;

import Application.dtos.leccion.LeccionLista;
//import Domain.models.CursoValueObjects.CursoId;//
import Domain.models.UsuarioValueObjects.UsuarioId;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controlador de la PANTALLA DE LECCIÓN:
 * - Recibe el contexto (usuario, curso) y la lección seleccionada (LeccionLista).
 * - Muestra título, tipo y estado.
 * - (Botones opcionales) Volver al curso / volver a la sección.
 */
public class LeccionController {

    @FXML private Label lblTitulo;
    @FXML private Label lblTipo;
    @FXML private Label lblEstado;
    @FXML private Button btnVolverCurso;
    @FXML private Button btnVolverSeccion;

    private UsuarioId usuarioId;
    //private CursoId cursoId;
    private LeccionLista leccion;

    /** Quien abre esta pantalla llama a este método. */
    public void setContext(UsuarioId usuarioId,  LeccionLista leccion) {
        this.usuarioId = usuarioId;
       // this.cursoId = cursoId;//
        this.leccion = leccion;
        pintar();
    }

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
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/Curso.fxml"));
            javafx.scene.Parent root = loader.load();
            // IMPORTANTE: para mantener el contexto deberías volver a llamar setContext(...)
            // Aquí no sabemos quién invoca, así que normalmente regreso a un "router" o repinto curso.
            javafx.stage.Stage stage = (javafx.stage.Stage) lblTitulo.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 1920, 1080)); stage.centerOnScreen(); stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "No se pudo volver: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void volverALaSeccion() {
        // Similar a volverAlCurso pero cargando Seccion.fxml
        // Si quieres conservar la sección exacta, debes pasar también el DTO de Seccion.
        new Alert(Alert.AlertType.INFORMATION, "Implementa esta navegación si usas pantalla de sección.").showAndWait();
    }
}
