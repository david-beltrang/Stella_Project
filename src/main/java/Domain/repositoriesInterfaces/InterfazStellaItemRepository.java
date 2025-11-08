// Domain/repositoriesInterfaces/StellaItemRepository.java
package Domain.repositoriesInterfaces;

import Domain.models.StellaItem;
import java.util.Optional;

public interface InterfazStellaItemRepository {
    Optional<StellaItem> findByItemId(int itemId);
}