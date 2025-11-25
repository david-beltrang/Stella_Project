// Application/services/UsuarioStellaService.java
package Application.services;

import Application.config.AppServices;
import Domain.models.StellaItem;
import Domain.models.UsuarioItem;
import Domain.repositoriesInterfaces.InterfazStellaItemRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Servicio para manejar la Stella actual del usuario.
 * Gestiona qué Stella está activa para el usuario y permite cambiarla.
 */
public class UsuarioStellaService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioStellaService.class);

    private final InterfazUsuarioItemRepository usuarioItemRepo;
    private final InterfazStellaItemRepository stellaItemRepo;

    public UsuarioStellaService(
            InterfazUsuarioItemRepository usuarioItemRepo,
            InterfazStellaItemRepository stellaItemRepo
    ) {
        this.usuarioItemRepo = usuarioItemRepo;
        this.stellaItemRepo = stellaItemRepo;
    }

    /**
     * Obtiene el ID del usuario actual desde AppServices.
     *
     * @return ID del usuario actual o null si no hay usuario logueado
     */
    private Integer obtenerUsuarioIdActual() {
        var usuario = AppServices.getUsuarioActual();
        if (usuario == null) {
            logger.warn("No hay usuario actual logueado");
            return null;
        }
        return usuario.id();
    }

    /**
     * Obtiene el UsuarioItem activo del usuario actual.
     *
     * @return Optional con el UsuarioItem activo, o empty si no hay usuario o no tiene items activos
     */
    public Optional<UsuarioItem> obtenerStellaActual() {
        Integer usuarioId = obtenerUsuarioIdActual();
        if (usuarioId == null) {
            return Optional.empty();
        }

        try {
            Optional<UsuarioItem> itemActivo = usuarioItemRepo.obtenerItemActivo(usuarioId);
            if (itemActivo.isPresent()) {
                logger.debug("Stella actual encontrada para usuario {}: itemId={}", usuarioId, itemActivo.get().getItemId());
            } else {
                logger.debug("Usuario {} no tiene ninguna Stella activa", usuarioId);
            }
            return itemActivo;
        } catch (Exception e) {
            logger.error("Error al obtener Stella actual para usuario {}", usuarioId, e);
            return Optional.empty();
        }
    }

    /**
     * Obtiene el StellaItem asociado al item activo del usuario.
     *
     * @return Optional con el StellaItem, o empty si no hay usuario, no tiene item activo, o el item no tiene Stella asociada
     */
    public Optional<StellaItem> obtenerStellaItemActual() {
        Optional<UsuarioItem> usuarioItem = obtenerStellaActual();
        if (usuarioItem.isEmpty()) {
            return Optional.empty();
        }

        int itemId = usuarioItem.get().getItemId();
        try {
            Optional<StellaItem> stellaItem = stellaItemRepo.findByItemId(itemId);
            if (stellaItem.isPresent()) {
                logger.debug("StellaItem encontrado para itemId={}", itemId);
            } else {
                logger.debug("No hay StellaItem asociado al itemId={}", itemId);
            }
            return stellaItem;
        } catch (Exception e) {
            logger.error("Error al obtener StellaItem para itemId={}", itemId, e);
            return Optional.empty();
        }
    }

    /**
     * Obtiene la ruta de la imagen de la Stella actual del usuario.
     *
     * @return Ruta de la imagen o null si no hay Stella activa
     */
    public String obtenerRutaImagenStellaActual() {
        Optional<StellaItem> stellaItem = obtenerStellaItemActual();
        return stellaItem.map(StellaItem::getImagePath).orElse(null);
    }

    /**
     * Cambia la Stella actual del usuario a un nuevo item.
     * Desactiva todos los items anteriores y activa el nuevo.
     * Si el item tiene un StellaItem asociado, se actualiza en la base de datos.
     *
     * @param nuevoItemId ID del item que se quiere activar como Stella actual
     * @throws RuntimeException si no hay usuario logueado, el usuario no tiene el item, o hay error en BD
     */
    public void cambiarStellaActual(int nuevoItemId) {
        Integer usuarioId = obtenerUsuarioIdActual();
        if (usuarioId == null) {
            throw new RuntimeException("No hay usuario logueado para cambiar la Stella actual");
        }

        // Verificar que el usuario tenga el item
        if (!usuarioItemRepo.tieneItem(usuarioId, nuevoItemId)) {
            throw new RuntimeException("El usuario no tiene el item con id: " + nuevoItemId);
        }

        try {
            // Cambiar el item activo en la base de datos
            usuarioItemRepo.cambiarItemActivo(usuarioId, nuevoItemId);
            logger.info("Stella actual cambiada para usuario {}: nuevo itemId={}", usuarioId, nuevoItemId);

            // Verificar si el item tiene un StellaItem asociado
            Optional<StellaItem> stellaItem = stellaItemRepo.findByItemId(nuevoItemId);
            if (stellaItem.isPresent()) {
                logger.debug("StellaItem asociado encontrado: {}", stellaItem.get().getImagePath());
            } else {
                logger.debug("El item {} no tiene StellaItem asociado", nuevoItemId);
            }
        } catch (Exception e) {
            logger.error("Error al cambiar Stella actual para usuario {} al item {}", usuarioId, nuevoItemId, e);
            throw new RuntimeException("Error al cambiar la Stella actual: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si el usuario tiene una Stella activa.
     *
     * @return true si el usuario tiene una Stella activa, false en caso contrario
     */
    public boolean tieneStellaActiva() {
        return obtenerStellaActual().isPresent();
    }
}

