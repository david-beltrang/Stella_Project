package Application.services.DarAcceso;


import Application.dtos.acceso.LoginRequest;
import Application.dtos.acceso.UsuarioResponse;
import Domain.models.Usuario;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import java.util.Optional;

/**
 * Servicio de aplicación para el caso de uso "dar acceso al usuario", que incluye registro, login y actualización de username.
 * Este servicio orquesta la lógica de negocio, interactúa con el repositorio de usuarios y usa DTOs para comunicarse con el frontend (JavaFX).
 * Proporciona el ID del usuario en las respuestas para asociar con sesiones de estudio u otras operaciones.
 */
public class LoginService {
    //Aquí se declara un atributo de tipo InterfazUsuarioRepository para poder utilizar los métodos presentes en la interfaz
    private final InterfazUsuarioRepository usuarioRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * parametro usuarioRepository Repositorio para operaciones de persistencia de usuarios.
     */
    public LoginService(InterfazUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Autentica a un usuario para iniciar sesión, verificando correo y contraseña.
     * parametro request DTO con el correo y contraseña del formulario de login.
     * retorna UsuarioResponse con los datos del usuario autenticado, incluyendo el ID para asociar con sesiones activas.
     * throws IllegalArgumentException si el correo no existe o la contraseña es incorrecta.
     */
    public UsuarioResponse login(LoginRequest request) {
        // Buscar usuario por correo
        // Se obtiene el usuario Optional buscandolo en la BD usando el correo recibido
        //del formulario del frontend. Este dato se conoce por el DTO LoginRequest
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorCorreo(request.correo());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Correo no encontrado");
        }
        //Si el Optional si retornó el objeto se hace un .get() para obtener el objeto
        Usuario usuario = usuarioOpt.get();
        // Verificar contraseña usando lógica del dominio de la clase Usuario
        //Si el dato de la contraseña que viene del front y se almacena en el DTO no coincide con
        //la contraseña de la instancia del usuario buscado por correo en la BD entonces se lanza la excepcion
        if (!usuario.verificarContrasena(request.contrasena())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
        // Se retorna un DTO con datos del usuario, incluyendo el ID para usar los datos
        // en la sesión
        return new UsuarioResponse(
                usuario.getId(), //Un get normal porque Id no es VO
                usuario.getUsername(),
                usuario.getCorreo(),
                usuario.getNombre(),
                usuario.getTipo().valor()
        );
    }


}