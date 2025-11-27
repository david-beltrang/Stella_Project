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

    void agregarPescaditos(int usuarioId, int cantidad);

    // Métodos para racha
    void actualizarRacha(int usuarioId, int nuevaRacha, java.time.LocalDateTime fechaUltimaLeccion);

    int obtenerRacha(int usuarioId);

    java.time.LocalDateTime obtenerFechaUltimaLeccion(int usuarioId);
}