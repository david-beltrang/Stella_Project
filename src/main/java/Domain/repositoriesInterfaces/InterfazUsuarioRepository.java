package Domain.repositoriesInterfaces;

import Domain.models.Usuario;
import Domain.models.UsuarioValueObjects.Username;
import java.util.List;
import java.util.Optional;

public interface InterfazUsuarioRepository {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(int id);
    Optional<Usuario> buscarPorCorreo(String correo);
    List<Usuario> listarTodos();
    void eliminar(int id);
    void actualizarUsername(int id, String nuevoUsername);
}