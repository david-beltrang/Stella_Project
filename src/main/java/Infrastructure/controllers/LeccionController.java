package Infrastructure.controllers;

import Application.dtos.leccion.LeccionLista;
import Domain.models.UsuarioValueObjects.UsuarioId;
import Infrastructure.ui.Navigacion;
import Infrastructure.ui.AyudaUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Function;

/**
 * Controlador de la pantalla de LECCIÓN.
 * - Recibe el contexto (usuario, curso) y la lección seleccionada (LeccionLista).
 * - Muestra título, tipo y estado.
 * - Permite volver a curso o sección.
 */
public class LeccionController {

    @FXML private Label lblTitulo;
    @FXML private Label lblTipo;
    @FXML private Label lblEstado;
    @FXML private Button btnVolverCurso;
    @FXML private Button btnVolverSeccion;

    private UsuarioId usuarioId;
    private LeccionLista leccion;
    private Function<Class<?>, Object> controllerFactory;

    private final Navigacion navigator = new Navigacion();
    private final AyudaUI uiHelper = new AyudaUI();

    public void setControllerFactory(Function<Class<?>, Object> controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public void setContext(UsuarioId usuarioId, LeccionLista leccion) {
        this.usuarioId = usuarioId;
        this.leccion = leccion;
        pintar();
    }

    private void pintar() {
        if (leccion == null) {
            uiHelper.showError("Error", "Lección no recibida");
            return;
        }
        lblTitulo.setText(leccion.titulo());
        lblTipo.setText("Tipo: " + leccion.tipoContenido());
        lblEstado.setText("Estado: " + leccion.estado().name());
    }

    @FXML
    private void volverAlCurso() {
        navigator.goTo("/views/Curso.fxml", "STELLA - Curso", controllerFactory, btnVolverCurso);
    }

    @FXML
    private void volverALaSeccion() {
        navigator.goTo("/views/Seccion.fxml", "STELLA - Sección", controllerFactory, btnVolverSeccion);
    }
}

