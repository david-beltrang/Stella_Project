package Application.services.DarAcceso;

import Application.dtos.acceso.RegistrarUsuarioRequest;
import Application.dtos.acceso.UsuarioResponse;
import Domain.models.Usuario;
import Domain.repositoriesInterfaces.InterfazUsuarioRepository;
import Domain.exceptions.usuario.UsuarioYaExisteException;


/**
 * Servicio de aplicación para el caso de uso "dar acceso al usuario", que incluye registro, login y actualización de username.
 * Este servicio orquesta la lógica de negocio, interactúa con el repositorio de usuarios y usa DTOs para comunicarse con el frontend (JavaFX).
 * Proporciona el ID del usuario en las respuestas para asociar con sesiones de estudio u otras operaciones.
 */
public class RegistroService {
    //Aquí se declara un atributo de tipo InterfazUsuarioRepository para poder utilizar los métodos presentes en la interfaz
    private final InterfazUsuarioRepository usuarioRepository;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     * parametro usuarioRepository Repositorio para operaciones de persistencia de usuarios.
     */
    public RegistroService(InterfazUsuarioRepository usuarioRepository) {
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


}