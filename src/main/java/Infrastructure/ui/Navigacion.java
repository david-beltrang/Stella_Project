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

/**
 * Clase utilitaria para navegación entre pantallas en JavaFX.
 * Permite cambiar vistas y opcionalmente inicializar el controlador antes de mostrarla.
 */
public class Navigacion {

    /**
     * Navega a una vista FXML básica (sin inicialización de controlador adicional).
     * Se asegura de no hacer cast forzado al tipo de controlador,
     * evitando errores como "QuizController cannot be cast to LeccionController".
     */
    public void goTo(String fxmlPath,
                     String title,
                     Function<Class<?>, Object> controllerFactory,
                     Node origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Aplica la factory global si existe
            if (controllerFactory != null) {
                loader.setControllerFactory(controllerFactory::apply);
            }

            Parent next = loader.load();

            // Obtiene el stage activo desde el nodo de origen
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
     * Carga un FXML y permite inicializar su controlador ANTES de mostrar la escena.
     * Útil para pasar datos (por ejemplo, usuario, curso, etc.) al siguiente controlador.
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

            // Inicializa el controlador genérico sin asumir su tipo
            Object ctrl = loader.getController();
            if (initController != null && ctrl != null) {
                try {
                    @SuppressWarnings("unchecked")
                    T controller = (T) ctrl;
                    initController.accept(controller);
                } catch (ClassCastException ignored) {
                    System.err.println("⚠️ Tipo de controlador distinto, se omitió la inicialización específica.");
                }
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

        new Alert(
                Alert.AlertType.ERROR,
                "No pude abrir la vista: " + fxmlPath + "\n\n" +
                        (cause != null ? cause.getMessage() : e.getMessage())
        ).showAndWait();
    }

    /**
     * Alternativa rápida para cambiar pantalla desde un botón.
     */
    public void cambiarPantalla(String fxmlPath, Button origen) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = resolveStage(origen);
            stage.setScene(new Scene(root));
            stage.setTitle("STELLA");
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            mostrarError(fxmlPath, e);
        }
    }
}
