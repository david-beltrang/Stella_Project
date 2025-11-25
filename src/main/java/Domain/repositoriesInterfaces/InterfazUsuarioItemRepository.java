// Domain/repositoriesInterfaces/UsuarioItemRepository.java
package Domain.repositoriesInterfaces;

import Domain.models.UsuarioItem;
import java.util.List;
import java.util.Optional;

public interface InterfazUsuarioItemRepository {
    void comprarItem(int usuarioId, int itemId, int costo);
    List<UsuarioItem> findByUsuarioId(int usuarioId);
    boolean tieneItem(int usuarioId, int itemId);
    int obtenerPescaditos(int usuarioId);
    Optional<UsuarioItem> obtenerItemActivo(int usuarioId);
    void cambiarItemActivo(int usuarioId, int nuevoItemId);
}