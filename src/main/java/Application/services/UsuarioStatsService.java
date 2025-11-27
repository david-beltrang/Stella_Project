package Application.services;

import Application.config.AppServices;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio centralizado para manejar las estadísticas del usuario:
 * - Pescaditos (moneda del juego)
 * - Racha de días
 * - Stella actual
 * 
 * Este servicio actúa como fachada para simplificar el acceso a las
 * estadísticas del usuario.
 */
public class UsuarioStatsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioStatsService.class);

    private final InterfazUsuarioItemRepository usuarioItemRepo;
    private final UsuarioStellaService stellaService;

    public UsuarioStatsService(
            InterfazUsuarioItemRepository usuarioItemRepo,
            UsuarioStellaService stellaService) {
        this.usuarioItemRepo = usuarioItemRepo;
        this.stellaService = stellaService;
    }

    /**
     * Obtiene el ID del usuario actual desde AppServices.
     * 
     * @return ID del usuario actual o null si no hay usuario logueado
     */
    public Integer obtenerUsuarioIdActual() {
        var usuario = AppServices.getUsuarioActual();
        if (usuario == null) {
            logger.warn("No hay usuario actual logueado");
            return null;
        }
        return usuario.id();
    }

    /**
     * Obtiene la cantidad de pescaditos del usuario actual.
     * 
     * @return Cantidad de pescaditos, o 0 si no hay usuario logueado o hay error
     */
    public int obtenerPescaditos() {
        Integer usuarioId = obtenerUsuarioIdActual();
        if (usuarioId == null) {
            return 0;
        }

        try {
            int pescaditos = usuarioItemRepo.obtenerPescaditos(usuarioId);
            logger.debug("Pescaditos del usuario {}: {}", usuarioId, pescaditos);
            return pescaditos;
        } catch (Exception e) {
            logger.error("Error al obtener pescaditos del usuario {}", usuarioId, e);
            return 0;
        }
    }

    /**
     * Obtiene la cantidad de pescaditos de un usuario específico.
     * 
     * @param usuarioId ID del usuario
     * @return Cantidad de pescaditos, o 0 si hay error
     */
    public int obtenerPescaditos(int usuarioId) {
        try {
            int pescaditos = usuarioItemRepo.obtenerPescaditos(usuarioId);
            logger.debug("Pescaditos del usuario {}: {}", usuarioId, pescaditos);
            return pescaditos;
        } catch (Exception e) {
            logger.error("Error al obtener pescaditos del usuario {}", usuarioId, e);
            return 0;
        }
    }

    /**
     * Obtiene la racha de días del usuario actual.
     * TODO: Implementar cuando exista el método en el repositorio
     * 
     * @return Racha de días, o 0 si no hay usuario logueado
     */
    public int obtenerRachaDias() {
        Integer usuarioId = obtenerUsuarioIdActual();
        if (usuarioId == null) {
            return 0;
        }

        try {
            int racha = usuarioItemRepo.obtenerRacha(usuarioId);
            logger.debug("Racha de días del usuario {}: {}", usuarioId, racha);
            return racha;
        } catch (Exception e) {
            logger.error("Error al obtener racha del usuario {}", usuarioId, e);
            return 0;
        }
    }

    /**
     * Obtiene la ruta de la imagen de la Stella actual del usuario.
     * Delega al UsuarioStellaService.
     * 
     * @return Ruta de la imagen o null si no hay Stella activa
     */
    public String obtenerRutaImagenStellaActual() {
        return stellaService.obtenerRutaImagenStellaActual();
    }

    /**
     * Cambia la Stella actual del usuario.
     * Delega al UsuarioStellaService.
     * 
     * @param nuevoItemId ID del item que se quiere activar como Stella actual
     * @throws RuntimeException si no hay usuario logueado, el usuario no tiene el
     *                          item, o hay error en BD
     */
    public void cambiarStellaActual(int nuevoItemId) {
        stellaService.cambiarStellaActual(nuevoItemId);
    }

    /**
     * Verifica si el usuario tiene una Stella activa.
     * Delega al UsuarioStellaService.
     * 
     * @return true si el usuario tiene una Stella activa, false en caso contrario
     */

    /**
     * Verifica si el usuario tiene una Stella activa.
     */
    public boolean tieneStellaActiva() {
        return stellaService.tieneStellaActiva();
    }

    /**
     * Agrega pescaditos al usuario.
     * 
     * @param usuarioId ID del usuario
     * @param cantidad  Cantidad de pescaditos a agregar
     */
    public void agregarPescaditos(int usuarioId, int cantidad) {
        try {
            usuarioItemRepo.agregarPescaditos(usuarioId, cantidad);
            logger.info("Se agregaron {} pescaditos al usuario {}", cantidad, usuarioId);
        } catch (Exception e) {
            logger.error("Error al agregar pescaditos al usuario {}", usuarioId, e);
            throw e;
        }
    }
}
