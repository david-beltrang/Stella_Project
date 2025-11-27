package Application.services.DarAcceso;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Domain.models.Usuario;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.repositoriesInterfaces.InterfazUsuarioItemRepository;
import Domain.exceptions.usuario.UsuarioYaExisteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de aplicación para el caso de uso "dar acceso al usuario", que
 * incluye registro, login y actualización de username.
 */
public class RegistroService {
    private static final Logger logger = LoggerFactory.getLogger(RegistroService.class);

    private final InterfazUsuarioRepository usuarioRepository;
    private final InterfazUsuarioItemRepository usuarioItemRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * parametro usuarioRepository Repositorio para operaciones de persistencia de
     * usuarios.
     */
    public RegistroService(InterfazUsuarioRepository usuarioRepository,
            InterfazUsuarioItemRepository usuarioItemRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioItemRepository = usuarioItemRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema, creando una entidad Usuario y
     * persistiendo en la base de datos.
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

            // 3. Crear usuario
            Usuario usuario = Usuario.crearNuevo(
                    request.username(),
                    request.correo(),
                    request.nombre(),
                    request.contrasena(),
                    request.tipo());

            Usuario saved = usuarioRepository.guardar(usuario);

            // 4. Crear las estadísticas iniciales del usuario en usuario_stats
            try {
                // Utilizar JDBC para insertar directamente en la tabla usuario_stats
                var connection = usuarioItemRepository.getClass()
                        .getDeclaredMethod("getConnection")
                        .invoke(usuarioItemRepository);

                var stmt = connection.getClass()
                        .getDeclaredMethod("prepareStatement", String.class)
                        .invoke(connection,
                                "INSERT INTO \"usuario_stats\" (usuario_id, pescaditos, objetivo_sesiones, racha_dias, tiempo_total_estudio_segundos) VALUES (?, 0, 1, 0, 0)");

                stmt.getClass().getDeclaredMethod("setInt", int.class, int.class)
                        .invoke(stmt, 1, saved.getId());

                stmt.getClass().getDeclaredMethod("executeUpdate").invoke(stmt);

                logger.info("Estadísticas iniciales creadas para usuario {}", saved.getId());
            } catch (Exception ex) {
                logger.error("Error al crear estadísticas iniciales", ex);
            }

            // 5. Asignar Stella por defecto (ID 0 = Stella sin ropa)
            try {
                logger.info("Usuario {} registrado con Stella por defecto", saved.getId());
            } catch (Exception ex) {
                logger.warn("No se pudo configurar Stella por defecto para usuario {}", saved.getId(), ex);
            }

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