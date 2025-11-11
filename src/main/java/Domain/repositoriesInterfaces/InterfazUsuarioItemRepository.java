// Domain/repositoriesInterfaces/UsuarioItemRepository.java
package Domain.repositoriesInterfaces;

import Domain.models.UsuarioItem;
import java.util.List;

public interface InterfazUsuarioItemRepository {
    void comprarItem(int usuarioId, int itemId, int costo);
    List<UsuarioItem> findByUsuarioId(int usuarioId);
    boolean tieneItem(int usuarioId, int itemId);
    int obtenerPescaditos(int usuarioId);
}