package Application.services;

import Application.dtos.RegistrarUsuarioRequest;
import Application.dtos.LoginRequest;
import Application.dtos.ActualizarUsernameRequest;
import Application.dtos.UsuarioResponse;
import Domain.models.Usuario;
import Domain.models.UsuarioValueObjects.Username;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.exceptions.UsuarioYaExisteException;

import java.util.Optional;

/**
 * Servicio de aplicación para el caso de uso "dar acceso al usuario", que incluye registro, login y actualización de username.
 * Este servicio orquesta la lógica de negocio, interactúa con el repositorio de usuarios y usa DTOs para comunicarse con el frontend (JavaFX).
 * Proporciona el ID del usuario en las respuestas para asociar con sesiones de estudio u otras operaciones.
 */
public class DarAccesoService {
    //Aquí se declara un atributo de tipo InterfazUsuarioRepository para poder utilizar los métodos presentes en la interfaz
    private final InterfazUsuarioRepository usuarioRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * parametro usuarioRepository Repositorio para operaciones de persistencia de usuarios.
     */
    public DarAccesoService(InterfazUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en el sistema, creando una entidad Usuario y persistiendo en la base de datos.
     * parametro request DTO con los datos del formulario de registro (username, correo, nombre, contraseña, tipo).
     * retorna UsuarioResponse DTO con los datos del usuario registrado, incluyendo el ID generado para uso en sesiones de estudio.
     * throws IllegalArgumentException si el correo ya está registrado o los datos son inválidos.
     * throws RuntimeException para errores inesperados (e.g., problemas de base de datos).
     */
    public UsuarioResponse registrar(RegistrarUsuarioRequest request) {
        try {
            // Crear usuario usando el factory method del dominio, que valida los value objects
            Usuario usuario = Usuario.crearNuevo(
                    request.username(),
                    request.correo(),
                    request.nombre(),
                    request.contrasena(),
                    request.tipo()
            );
            // Guardar en el repositorio, que retorna el Usuario con el ID generado
            //saved queda con el objeto del usuario obtenido de la BD
            Usuario saved = usuarioRepository.guardar(usuario);
            // Retornar DTO para el frontend, excluyendo la contraseña por seguridad
            return new UsuarioResponse(
                    saved.getId(), //Se hace un get normal porque el Id no es un VO
                    saved.getUsername().valor(), //Se hace un getUsername().valor() porque el record del VO tiene el método para obtener el valor
                    saved.getCorreo().valor(), //Se hace un getNombre().valor() porque el record del VO tiene el método para obtener el valor
                    saved.getNombre().valor(), //Se hace un getNombre().valor() porque el record del VO tiene el método para obtener el valor
                    saved.getTipo().valor() //Se hace un getTipo().valor() porque el record del VO tiene el método para obtener el valor
            );
        } catch (UsuarioYaExisteException e) {
            // Manejar caso de correo duplicado
            // Convierte la excepcion de dominio
            throw new IllegalArgumentException("El correo ya está registrado: " + e.getMessage());
        } catch (Exception e) {
            // Capturar errores de validación
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
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
                usuario.getUsername().valor(), //get().valor() dado que es value object
                usuario.getCorreo().valor(),
                usuario.getNombre().valor(),
                usuario.getTipo().valor()
        );
    }

    /**
     * Actualiza el username de un usuario existente.
     * recibe como parametro request DTO con el ID del usuario y el nuevo username propuesto.
     * throws IllegalArgumentException si el usuario no existe o el nuevo username es inválido.
     */
    public void actualizarUsername(ActualizarUsernameRequest request) {
        // Verificar que el usuario existe
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(request.id());
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario con ID " + request.id() + " no encontrado");
        }
        // Actualizar username en la base de datos, validando unicidad en el repositorio
        //Se llama el método de la interfaz del repositorio con el parámetro del request.id() y se instancia el VO
        //para que se compruebe que el username que viene desde el front cumpla con las reglas de negocio y dominio
        //y a este new se le manda como parámetro el String del DTO (ActualizarUsernameRequest)
        Username UsernameNuevo = new Username(request.nuevoUsername());
        usuarioRepository.actualizarUsername(request.id(), UsernameNuevo.valor());
    }
}