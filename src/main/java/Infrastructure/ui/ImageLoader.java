// Infrastructure/ui/ImageLoader.java
package Infrastructure.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Servicio utilitario para cargar imágenes en ImageViews.
 * Aplica Information Expert: este servicio es experto en cargar imágenes.
 * Aplica Separation of Concerns: separa la lógica de carga de imágenes de los controladores.
 */
public class ImageLoader {
    private static final Logger logger = LoggerFactory.getLogger(ImageLoader.class);
    private static final double DEFAULT_IMAGE_SIZE = 150.0;

    /**
     * Carga una imagen en un ImageView desde una ruta de recurso.
     * Aplica Factory Method para crear y configurar la imagen.
     *
     * @param destino ImageView donde cargar la imagen
     * @param rawPath Ruta relativa del recurso (ej: "/Image/General/logo.png")
     * @return true si la imagen se cargó correctamente, false en caso contrario
     */
    public boolean cargarImagen(ImageView destino, String rawPath) {
        if (destino == null) {
            logger.warn("ImageView destino es null");
            return false;
        }

        String path = normalizarRuta(rawPath);
        if (path == null) {
            return false;
        }

        URL url = getClass().getResource(path);
        if (url == null) {
            logger.warn("Imagen no encontrada: {}", path);
            return false;
        }

        try {
            Image image = new Image(url.toExternalForm());
            destino.setImage(image);
            destino.setPreserveRatio(true);
            
            // Configurar tamaño por defecto si no está establecido
            if (destino.getFitWidth() <= 0 && destino.getFitHeight() <= 0) {
                destino.setFitWidth(DEFAULT_IMAGE_SIZE);
                destino.setFitHeight(DEFAULT_IMAGE_SIZE);
            }
            
            logger.debug("Imagen cargada correctamente: {}", path);
            return true;
        } catch (Exception e) {
            logger.error("Error al cargar imagen desde: {}", path, e);
            return false;
        }
    }

    /**
     * Normaliza la ruta del recurso asegurándose de que comience con "/".
     *
     * @param rawPath Ruta original
     * @return Ruta normalizada o null si es inválida
     */
    private String normalizarRuta(String rawPath) {
        if (rawPath == null || rawPath.trim().isBlank()) {
            return null;
        }
        
        String path = rawPath.trim();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}

