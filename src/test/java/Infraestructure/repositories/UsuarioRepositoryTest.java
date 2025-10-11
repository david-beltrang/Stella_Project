package Infrastructure.repositories;

import Domain.exceptions.UsuarioYaExisteException;
import Domain.models.Usuario;
import Infrastructure.persistence.ConexionBD;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioRepositoryTest {

    UsuarioRepository repo;

    @BeforeEach
    void setup() throws Exception {
        repo = new UsuarioRepository(); // crea tabla si no existe

        // deja la tabla limpia antes de cada test
        try (Connection c = ConexionBD.getConnection(); Statement st = c.createStatement()) {
            st.execute("DELETE FROM usuarios");
        }
    }

    @Test @Order(1)
    void guardar_y_buscarPorCorreo_ok() {
        var nuevo = Usuario.crearNuevo("dan","dan@puj.edu.co","Daniel","secret","ESTUDIANTE");
        var saved = repo.guardar(nuevo);

        assertNotNull(saved.getId());
        assertEquals("dan", saved.getUsername().valor());

        var encontrado = repo.buscarPorCorreo("dan@puj.edu.co").orElseThrow();
        assertEquals(saved.getId(), encontrado.getId());
        assertEquals("Daniel", encontrado.getNombre().valor());
    }

    @Test @Order(2)
    void correoDuplicado_lanzaUsuarioYaExisteException() {
        var u1 = Usuario.crearNuevo("u1","a@a.com","Ana","p","ESTUDIANTE");
        repo.guardar(u1);

        var u2 = Usuario.crearNuevo("u2","a@a.com","Benito","p","ESTUDIANTE");
        assertThrows(UsuarioYaExisteException.class, () -> repo.guardar(u2));
    }

    @Test @Order(3)
    void listar_eliminar_y_actualizarUsername() {
        var u1 = repo.guardar(Usuario.crearNuevo("u1","u1@x.com","Uno","p","ESTUDIANTE"));
        var u2 = repo.guardar(Usuario.crearNuevo("u2","u2@x.com","Dos","p","ESTUDIANTE"));

        assertEquals(2, repo.listarTodos().size());

        repo.actualizarUsername(u1.getId(), "nuevoU1");
        var actualizado = repo.buscarPorId(u1.getId()).orElseThrow();
        assertEquals("nuevoU1", actualizado.getUsername().valor());

        repo.eliminar(u2.getId());
        assertTrue(repo.buscarPorId(u2.getId()).isEmpty());
    }
}
