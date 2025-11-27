package Application.services.DarAcceso;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Domain.models.Usuario;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioStatsRepository;
import Domain.exceptions.usuario.UsuarioYaExisteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de aplicación para el caso de uso "dar acceso al usuario", que
 * incluye registro, login y actualización de username.
 * Este servicio orquesta la lógica de negocio, interactúa con el repositorio de
 * usuarios y usa DTOs para comunicarse con el frontend (JavaFX).
 * Proporciona el ID del usuario en las respuestas para asociar con sesiones de
 * estudio u otras operaciones.
 */
public class RegistroService {
    private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

    // Aquí se declara un atributo de tipo InterfazUsuarioRepository para poder
    // utilizar los métodos presentes en la interfaz
    private final InterfazUsuarioRepository usuarioRepository;
    private final InterfazUsuarioItemRepository usuarioItemRepository;
    private final InterfazUsuarioStatsRepository usuarioStatsRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * parametro usuarioRepository Repositorio para operaciones de persistencia de
     * usuarios.
     */
    public RegistroService(InterfazUsuarioRepository usuarioRepository,
            InterfazUsuarioItemRepository usuarioItemRepository,
            InterfazUsuarioStatsRepository usuarioStatsRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioItemRepository = usuarioItemRepository;
        this.usuarioStatsRepository = usuarioStatsRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema, creando una entidad Usuario y
     * persistiendo en la base de datos.
     * parametro request DTO con los datos del formulario de registro (username,
     * correo, nombre, contraseña, tipo).
     * retorna UsuarioResponse DTO con los datos del usuario registrado, incluyendo
     * el ID generado para uso en sesiones de estudio.
     * throws IllegalArgumentException si el correo ya está registrado o los datos
     * son inválidos.
     * throws RuntimeException para errores inesperados (e.g., problemas de base de
     * datos).
     */
    public UsuarioResponse registrar(RegistrarUsuarioRequest request) {
        try {
            // 1. Validar username duplicado
            if (usuarioRepository.buscarPorUsername(request.username()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya está en uso");
            }

            // 2. Validar correo duplicado (tu forma, perfecta)
            if (usuarioRepository.buscarPorCorreo(request.correo()).isPresent()) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }

            // 3. Crear usuario → aquí el factory valida:
            // - tipo de usuario válido
            // - contraseña fuerte
            // - correo formato válido
            // → si falla, lanza IllegalArgumentException directamente
            Usuario usuario = Usuario.crearNuevo(
                    request.username(),
                    request.correo(),
                    request.nombre(),
                    request.contrasena(),
                    request.tipo() //
            );

            Usuario saved = usuarioRepository.guardar(usuario);

            // 4. Crear las estadísticas iniciales del usuario en usuario_stats
            usuarioStatsRepository.crearStatsIniciales(saved.getId());

            logger.info("Usuario {} registrado exitosamente", saved.getId());

            return new UsuarioResponse(
                    saved.getId(),
                    saved.getUsername(),
                    saved.getCorreo(),
                    saved.getNombre(),
                    saved.getTipo().valor());

        } catch (UsuarioYaExisteException e) {
            // Por si el repo lanza (doble seguridad)
            throw new IllegalArgumentException("El correo ya está registrado");
        } catch (IllegalArgumentException e) {
            // Captura: tipo inválido, contraseña débil, formato correo, etc.
            throw e; // Re-lanzar tal cual
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al registrar usuario", e);
        }
    }

}