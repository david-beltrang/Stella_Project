package Infrastructure.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Navegacion {
    private static final Logger logger = LoggerFactory.getLogger(Navegacion.class);

    /** Navegación simple sin inicialización adicional */
    public void goTo(String fxmlPath, String title, Function<Class<?>, Object> controllerFactory, Node origen) {
        cargarYMostrarVista(fxmlPath, title, controllerFactory, origen, null);
    }

    /** Navegación con inicialización previa del controlador */
    public <T> void goToWithInit(String fxmlPath, String title, Function<Class<?>, Object> controllerFactory,
            Node origen, Consumer<T> initController) {
        cargarYMostrarVista(fxmlPath, title, controllerFactory, origen, initController);
    }

    /** Método centralizado para cargar y mostrar vistas. */
    private <T> void cargarYMostrarVista(String fxmlPath, String title, Function<Class<?>, Object> controllerFactory,
            Node origen, Consumer<T> initController) {
        try {
            logger.debug("Intentando cargar vista: {}", fxmlPath);
            var resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalArgumentException("No se encontró el recurso FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            if (controllerFactory != null)
                loader.setControllerFactory(controllerFactory::apply);

            Parent next = loader.load();

            // Inicializa el controlador (si corresponde)
            Object ctrl = loader.getController();
            if (initController != null && ctrl != null) {
                try {
                    @SuppressWarnings("unchecked")
                    T controller = (T) ctrl;
                    initController.accept(controller);
                } catch (ClassCastException e) {
                    logger.warn("Tipo de controlador distinto, se omitió la inicialización específica", e);
                }
            }

            // Resolver stage actual
            Stage stage = resolveStage(origen);
            if (stage == null) {
                logger.warn("No se encontró Stage a partir del nodo. Buscando uno visible...");
                stage = buscarStageVisible();
            }

            if (stage == null) {
                throw new IllegalStateException("No hay Stage activo para mostrar la vista.");
            }

            logger.debug("Stage detectado: {}", stage);
            stage.setScene(new Scene(next));

            if (title != null && !title.isBlank())
                stage.setTitle(title);

            // Maximizar para adaptar al tamaño de pantalla
            stage.setMaximized(true);
            stage.show();

            logger.info("Vista cargada correctamente: {}", fxmlPath);

        } catch (Exception e) {
            mostrarError(fxmlPath, e);
        }
    }

    /** Determina el Stage actual a partir de un nodo, o busca el primero activo. */
    private Stage resolveStage(Node origen) {
        try {
            if (origen != null && origen.getScene() != null) {
                var stage = (Stage) origen.getScene().getWindow();
                if (stage != null)
                    return stage;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Busca cualquier Stage visible si el origen no tiene uno asociado */
    private Stage buscarStageVisible() {
        for (Window w : Window.getWindows()) {
            if (w instanceof Stage s && w.isShowing()) {
                logger.debug("Usando stage visible encontrado: {}", s);
                return s;
            }
        }
        logger.warn("No se encontró ningún stage visible");
        return null;
    }

    /** Muestra diálogo de error detallado. */
    private void mostrarError(String fxmlPath, Exception e) {
        logger.error("Error al cargar vista: {}", fxmlPath, e);
        Throwable cause = e;
        while (cause.getCause() != null)
            cause = cause.getCause();

        new Alert(Alert.AlertType.ERROR,
                "Error al abrir vista:\n" + fxmlPath +
                        "\n\n" + (cause != null ? cause.getMessage() : e.getMessage()))
                .showAndWait();
    }

    /** Atajo rápido para cambiar de pantalla desde un botón */
    public void cambiarPantalla(String fxmlPath, Button origen) {
        goTo(fxmlPath, "STELLA", null, origen);
    }
}