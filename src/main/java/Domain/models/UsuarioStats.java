package Domain.models;

// ... imports necesarios ...

public class UsuarioStats {

    // El ID de la tabla usuario_stats es una FK/PK al usuario, por lo que usamos el usuarioId
    private final int usuarioId;
    private int pescaditos;
    private int objetivoSesiones;
    private int diasRacha;

    private UsuarioStats(int usuarioId, int pescaditos, int objetivoSesiones, int diasRacha) {
        this.usuarioId = usuarioId;
        this.pescaditos = pescaditos;
        this.objetivoSesiones = objetivoSesiones;
        this.diasRacha = diasRacha;
    }

    // --- FACTORY METHODS ---

    // Usado al crear un nuevo usuario
    public static UsuarioStats crearInicial(int usuarioId) {
        // Valores por defecto de tu BD: pescaditos=0, objetivo_sesiones=1, dias_racha=0
        return new UsuarioStats(usuarioId, 0, 1, 0);
    }

    // Usado por el Repositorio para reconstruir desde la BD
    public static UsuarioStats reconstruir(int usuarioId, int pescaditos, int objetivoSesiones, int diasRacha) {
        return new UsuarioStats(usuarioId, pescaditos, objetivoSesiones, diasRacha);
    }

    // --- COMPORTAMIENTOS y LÓGICA DEL DOMINIO ---

    public void ganarPescaditos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva para sumar.");
        }
        this.pescaditos += cantidad;
    }

    public void actualizarRacha(int dias) {
        // La lógica compleja de "romper racha" o "sumar día" reside aquí.
        // Por ejemplo: if (dias == 1) this.diasRacha++; else this.diasRacha = 0;
        this.diasRacha = dias;
    }

    // ... Getters ...
    public int getPescaditos() { return pescaditos; }
    // etc.
}