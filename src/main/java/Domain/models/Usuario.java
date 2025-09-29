package Domain.models;

import Domain.models.UsuarioValueObjects.Correo;
import Domain.models.UsuarioValueObjects.Nombre;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.models.UsuarioValueObjects.Username;

import java.util.Objects;

/**
 * Entidad Usuario (Aggregate Root).
 * - Contiene Value Objects (Username, Correo, Nombre).
 * - Métodos de comportamiento: verificarContrasena, verificarCorreo actualizarUsername.
 * - Factory method crearNuevo para crear desde primitivas (formulario).
 * - Factory reconstruir para reconstruir desde la BD (repositorio).
 */
public class Usuario {

    private Integer id;            //null si no se ha guardado el usuario en la BD
    private Username username;     // Value Object
    private Correo correo;         // Value Object
    private Nombre nombre;         // Value Object
    private String contrasena;     // contraseña simple (tu requerimiento)
    private Tipo tipo;      // Value Object

    // Constructor garantiza integridad del objeto y que los valores no sean nulos
    private Usuario(Integer id,
                    Username username,
                    Correo correo,
                    Nombre nombre,
                    String contrasena,
                    Tipo tipo) {

        this.id = id; //antes de ingresar a la BD es nulo
        this.username = Objects.requireNonNull(username, "username no puede ser nulo");
        this.correo = Objects.requireNonNull(correo, "correo no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser nulo");
        this.contrasena = Objects.requireNonNull(contrasena, "contrasena no puede ser nula");
        this.tipo = Objects.requireNonNull(tipo, "tipo no puede ser nulo");
    }

    // ------------------FACTORY METHODS ----------------------------------------

    /*
     * Crear un nuevo Usuario desde datos que vienen desde el formulario de resgistro (lo que usaría el caso de uso "registrarse").
     * id = null ya que se asignará al insertar en la BD.
     */
    public static Usuario crearNuevo(String usernameStr,
                                     String correoStr,
                                     String nombreStr,
                                     String contrasena,
                                     String tipoStr) {

        Username usernameVo = new Username(usernameStr); // lanza excepción si inválido
        Correo correoVo = new Correo(correoStr);        // lanza excepción si inválido
        Nombre nombreVo = new Nombre(nombreStr);        // lanza excepción si inválido
        Tipo tipoVo = new Tipo(tipoStr);
        return new Usuario(null, usernameVo, correoVo, nombreVo, contrasena, tipoVo);
    }

    /*
     * Reconstruir un Usuario instanciandoolo desde datos que vienen de la BD.
     * Este metodo es usado por la implementación del repositorio al leer la fila.
     */

    public static Usuario reconstruir(Integer id,
                                      Username username,
                                      Correo correo,
                                      Nombre nombre,
                                      String contrasena,
                                      Tipo tipo) {
        return new Usuario(id, username, correo, nombre, contrasena, tipo);
    }

    // ---------- COMPORTAMIENTOS y LÓGICA DEL DOMINIO ----------

    /*
     * Verifica si la contraseña ingresada coincide con la del usuario en memoria.
     * Esta comparación es parte del dominio.
     */
    public boolean verificarContrasena(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }

    /*
     * Verifica si el correo ingresado coincide con el del usuario en memoria.
     * Esta comparación es parte del dominio.
     */
    public boolean verificarCorreo(String correoIngresado) {
        return this.correo.equals(new Correo(correoIngresado));
    }

    /*
     * Actualiza el username del usuario y realiza validación del dato de entrada mediante al VO Username.
     * Este metodo cambia el estado del agregado username.
     */
    public void actualizarUsername(String nuevoUsername) {
        this.username = new Username(nuevoUsername); // si  es inválido, el VO lanzará excepción
    }

    // ---------- GETTERS  ---------
    public Integer getId() { return id; }
    public Username getUsername() { return username; }
    public Correo getCorreo() { return correo; }
    public Nombre getNombre() { return nombre; }
    public String getContrasena() { return contrasena; }
    public Tipo getTipo() { return tipo; }


}
