package Domain.models;

import Domain.models.UsuarioValueObjects.Correo;
import Domain.models.UsuarioValueObjects.Nombre;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.models.UsuarioValueObjects.Username;

import java.util.Objects;

/**
 * Entidad Usuario (Aggregate Root).
 * - Contiene Value Objects (Username, Correo, Nombre).
 * package Domain.models;
 * 
 * import Domain.models.UsuarioValueObjects.Correo;
 * import Domain.models.UsuarioValueObjects.Nombre;
 * import Domain.models.UsuarioValueObjects.Tipo;
 * import Domain.models.UsuarioValueObjects.Username;
 * 
 * import java.util.Objects;
 * 
 * /**
 * Entidad Usuario (Aggregate Root).
 * - Contiene Value Objects (Username, Correo, Nombre).
 * - Métodos de comportamiento: verificarContrasena, verificarCorreo
 * actualizarUsername.
 * - Implementa Builder Pattern para su creación.
 */
public class Usuario {

    private final Integer id; // null si no se ha guardado el usuario en la BD
    private Username username; // Value Object (Mutable para permitir actualización)
    private final Correo correo; // Value Object
    private final Nombre nombre; // Value Object
    private final String contrasena; // contraseña simple (tu requerimiento)
    private final Tipo tipo; // Value Object
    private final java.time.LocalDateTime fechaCreacion; // Nuevo campo

    // Constructor privado para el Builder
    private Usuario(Builder builder) {
        this.id = builder.id;
        this.username = Objects.requireNonNull(builder.username, "username no puede ser nulo");
        this.correo = Objects.requireNonNull(builder.correo, "correo no puede ser nulo");
        this.nombre = Objects.requireNonNull(builder.nombre, "nombre no puede ser nulo");
        this.contrasena = Objects.requireNonNull(builder.contrasena, "contrasena no puede ser nula");
        this.tipo = Objects.requireNonNull(builder.tipo, "tipo no puede ser nulo");
        this.fechaCreacion = builder.fechaCreacion != null ? builder.fechaCreacion : java.time.LocalDateTime.now();
    }

    // ------------------ BUILDER PATTERN ----------------------------------------

    public static class Builder {
        private Integer id;
        private Username username;
        private Correo correo;
        private Nombre nombre;
        private String contrasena;
        private Tipo tipo;
        private java.time.LocalDateTime fechaCreacion;

        public Builder() {
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder username(String usernameStr) {
            this.username = new Username(usernameStr);
            return this;
        }

        public Builder username(Username username) {
            this.username = username;
            return this;
        }

        public Builder correo(String correoStr) {
            this.correo = new Correo(correoStr);
            return this;
        }

        public Builder correo(Correo correo) {
            this.correo = correo;
            return this;
        }

        public Builder nombre(String nombreStr) {
            this.nombre = new Nombre(nombreStr);
            return this;
        }

        public Builder nombre(Nombre nombre) {
            this.nombre = nombre;
            return this;
        }

        public Builder contrasena(String contrasena) {
            this.contrasena = contrasena;
            return this;
        }

        public Builder tipo(String tipoStr) {
            this.tipo = new Tipo(tipoStr);
            return this;
        }

        public Builder tipo(Tipo tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder fechaCreacion(java.time.LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Usuario build() {
            return new Usuario(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // ------------------ FACTORY METHODS (ADAPTED TO USE BUILDER)
    // ----------------------------------------

    /*
     * Crear un nuevo Usuario desde datos que vienen desde el formulario de
     * resgistro.
     */
    public static Usuario crearNuevo(String usernameStr,
            String correoStr,
            String nombreStr,
            String contrasena,
            String tipoStr) {
        return new Builder()
                .username(usernameStr)
                .correo(correoStr)
                .nombre(nombreStr)
                .contrasena(contrasena)
                .tipo(tipoStr)
                .fechaCreacion(java.time.LocalDateTime.now())
                .build();
    }

    /*
     * Reconstruir un Usuario instanciandoolo desde datos que vienen de la BD.
     */
    public static Usuario reconstruir(Integer id,
            Username username,
            Correo correo,
            Nombre nombre,
            String contrasena,
            Tipo tipo,
            java.time.LocalDateTime fechaCreacion) {
        return new Builder()
                .id(id)
                .username(username)
                .correo(correo)
                .nombre(nombre)
                .contrasena(contrasena)
                .tipo(tipo)
                .fechaCreacion(fechaCreacion)
                .build();
    }

    // ---------- COMPORTAMIENTOS y LÓGICA DEL DOMINIO ----------

    /*
     * Verifica si la contraseña ingresada coincide con la del usuario en memoria.
     */
    public boolean verificarContrasena(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }

    /*
     * Verifica si el correo ingresado coincide con el del usuario en memoria.
     */
    public boolean verificarCorreo(String correoIngresado) {
        return this.correo.equals(new Correo(correoIngresado));
    }

    /*
     * Actualiza el username del usuario.
     */
    public void actualizarUsername(String nuevoUsername) {
        this.username = new Username(nuevoUsername);
    }

    // ---------- GETTERS ---------
    public Integer getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public Correo getCorreo() {
        return correo;
    }

    public Nombre getNombre() {
        return nombre;
    }

    public String getContrasena() {
        return contrasena;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public java.time.LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
