package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query(value = "SELECT i FROM Item i " +
            "WHERE LOWER(i.name) LIKE LOWER(concat('%',?1,'%')) "
            + " OR LOWER(i.description) LIKE LOWER(concat('%',?1,'%'))"
            + " AND i.available = TRUE")
    List<Item> searchAvailableItems(String text);

    void deleteById(long itemId);
}
