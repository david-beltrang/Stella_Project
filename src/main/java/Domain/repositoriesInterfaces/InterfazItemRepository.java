// Domain/repositoriesInterfaces/ItemRepository.java
package Domain.repositoriesInterfaces;

import Domain.models.Item;
import java.util.List;
import java.util.Optional;

public interface InterfazItemRepository {
    List<Item> findAll();
    Optional<Item> findById(int id);
}