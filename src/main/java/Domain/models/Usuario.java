package Domain.models;

import Domain.models.UsuarioValueObjects.Tipo;
import java.util.Objects;

// Clase principal del usuario
public class Usuario {

    private final Integer id; // El ID es null si no está en base de datos
    private String username; // El username se puede cambiar
    private final String correo;
    private final String nombre;
    private final String contrasena;
    private final Tipo tipo;
    private final java.time.LocalDateTime fechaCreacion;

    // Constructor privado, solo el builder lo usa
    private Usuario(Builder builder) {
        this.id = builder.id;
        this.username = Objects.requireNonNull(builder.username, "username no puede ser nulo");
        this.correo = Objects.requireNonNull(builder.correo, "correo no puede ser nulo");
        this.nombre = Objects.requireNonNull(builder.nombre, "nombre no puede ser nulo");
        this.contrasena = Objects.requireNonNull(builder.contrasena, "contrasena no puede ser nula");
        this.tipo = Objects.requireNonNull(builder.tipo, "tipo no puede ser nulo");
        this.fechaCreacion = builder.fechaCreacion != null ? builder.fechaCreacion : java.time.LocalDateTime.now();
    }

    // Usamos un builder para crear el objeto más fácil
    public static class Builder {
        private Integer id;
        private String username;
        private String correo;
        private String nombre;
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
            this.username = usernameStr;
            return this;
        }

        public Builder correo(String correoStr) {
            this.correo = correoStr;
            return this;
        }

        public Builder nombre(String nombreStr) {
            this.nombre = nombreStr;
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

    // Métodos estáticos para crear usuarios
    // Crear un nuevo Usuario desde datos que vienen desde el formulario de registro
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

    // Reconstruir un Usuario instanciandolo desde datos que vienen de la BD
    public static Usuario reconstruir(Integer id,
            String username,
            String correo,
            String nombre,
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

    // Métodos de lógica
    // Verifica si la contraseña ingresada coincide con la del usuario en memoria
    public boolean verificarContrasena(String contrasenaIngresada) {
        return this.contrasena.equals(contrasenaIngresada);
    }

    // Verifica si el correo ingresado coincide con el del usuario en memoria
    public boolean verificarCorreo(String correoIngresado) {
        return this.correo == correoIngresado;
    }

    // Actualiza el username del usuario
    public void actualizarUsername(String nuevoUsername) {
        this.username = nuevoUsername;
    }

    // Getters normales
    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
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
