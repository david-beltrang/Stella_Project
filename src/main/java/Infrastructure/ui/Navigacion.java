package Infrastructure.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Clase utilitaria para navegación entre pantallas en JavaFX.
 * Permite cambiar vistas y opcionalmente inicializar el controlador antes de mostrarla.
 */
public class Navigacion {

    /**
     * Navega a una vista FXML básica (sin inicialización de controlador adicional).
     */
    public void goTo(String fxmlPath,
                     String title,
                     Function<Class<?>, Object> controllerFactory,
                     Node origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            Stage stage = resolveStage(origen);
            stage.setScene(new Scene(next));

            if (title != null && !title.isBlank()) {
                stage.setTitle(title);
            }

            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarError(fxmlPath, e);
        }
    }

    /**
     * Carga un FXML, permite inicializar el controlador ANTES de mostrar la escena.
     * Muy útil cuando se debe pasar información al siguiente controller (por ejemplo: Lección actual, usuario, etc.).
     */
    public <T> void goToWithInit(String fxmlPath,
                                 String title,
                                 Function<Class<?>, Object> controllerFactory,
                                 Node origen,
                                 Consumer<T> initController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            // Inicializar el controlador antes de mostrar la escena
            @SuppressWarnings("unchecked")
            T ctrl = (T) loader.getController();
            if (initController != null && ctrl != null) {
                initController.accept(ctrl);
            }

            Stage stage = resolveStage(origen);
            stage.setScene(new Scene(next));

            if (title != null && !title.isBlank()) {
                stage.setTitle(title);
            }

            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarError(fxmlPath, e);
        }
    }

    // ===== Métodos privados =====

    /**
     * Determina el Stage actual a partir de un nodo, o busca el primer Stage visible.
     */
    private Stage resolveStage(Node origen) {
        try {
            if (origen != null && origen.getScene() != null) {
                return (Stage) origen.getScene().getWindow();
            }
            for (Window w : Window.getWindows()) {
                if (w instanceof Stage s && w.isShowing()) {
                    return s;
                }
            }
        } catch (Exception ignored) {}
        throw new IllegalStateException("No hay Stage activo para navegar.");
    }

    /**
     * Muestra un diálogo de error con información detallada.
     */
    private void mostrarError(String fxmlPath, Exception e) {
        e.printStackTrace();
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR,
                "No pude abrir la vista: " + fxmlPath + "\n\n" +
                        (cause != null ? cause.getMessage() : e.getMessage())
        ).showAndWait();
    }

    public void cambiarPantalla(String fxmlPath, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage;
            if (origen != null && origen.getScene() != null) {
                stage = (Stage) origen.getScene().getWindow();
            } else {
                // Buscar un Stage visible si el botón es nulo o no tiene escena
                stage = null;
                for (Window w : Window.getWindows()) {
                    if (w instanceof Stage s && w.isShowing()) {
                        stage = s;
                        break;
                    }
                }
                if (stage == null) {
                    throw new IllegalStateException("No hay Stage activo para navegar.");
                }
            }

            stage.setScene(new Scene(root));
            stage.setTitle("STELLA");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarError(fxmlPath, e);
        }
    }

}
