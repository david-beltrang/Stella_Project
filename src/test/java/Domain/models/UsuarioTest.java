package Domain.models;

import Domain.models.UsuarioValueObjects.Correo;
import Domain.models.UsuarioValueObjects.Nombre;
import Domain.models.UsuarioValueObjects.Tipo;
import Domain.models.UsuarioValueObjects.Username;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void crearNuevo_y_comportamientos_basicos() {
        var u = Usuario.crearNuevo(
                "dan123", "dan@puj.edu.co", "Daniel", "secret", "ESTUDIANTE"
        );

        assertTrue(u.verificarContrasena("secret"));
        assertTrue(u.verificarCorreo("dan@puj.edu.co"));

        u.actualizarUsername("nuevoUser");
        assertEquals("nuevoUser", u.getUsername().valor());
    }

    @Test
    void reconstruir_desde_bd() {
        var u = Usuario.reconstruir(
                1,
                new Username("user1"),
                new Correo("user1@puj.edu.co"),
                new Nombre("User Uno"),
                "pwd",
                new Tipo("ESTUDIANTE")
        );
        assertEquals(1, u.getId());
        assertEquals("user1", u.getUsername().valor());
        assertEquals("ESTUDIANTE", u.getTipo().valor());
    }
}
