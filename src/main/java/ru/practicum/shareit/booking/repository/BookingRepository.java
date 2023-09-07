package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface  BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1")
    List<Booking> findAllByBooker(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.status = :#{#status}")
    List<Booking> findAllByBookerAndStatus(long bookerId, @Param("status") BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllPastByBooker(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findAllCurrentByBooker(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.booker.id = ?1 AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllFutureByBooker(long bookerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1")
    List<Booking> findAllByOwner(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.status = :#{#status}")
    List<Booking> findAllByOwnerAndStatus(long ownerId, @Param("status") BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findAllPastByOwner(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    List<Booking> findAllCurrentByOwner(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE i.ownerId = ?1 AND b.start > CURRENT_TIMESTAMP")
    List<Booking> findAllFutureByOwner(long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE b.item.id = ?1 AND b.start <= CURRENT_TIMESTAMP AND i.ownerId = ?2")
    List<Booking> getLastByItemId(long itemId, long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b INNER JOIN Item AS i ON b.item.id = i.id WHERE b.item.id = ?1 AND b.start > CURRENT_TIMESTAMP AND i.ownerId = ?2 AND b.status = 'APPROVED'")
    List<Booking> getNextByItemId(long itemId, long ownerId, Sort sort);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.status != :#{#status}")
    List<Booking> findAllByItemIdAndStatus(long itemId, @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking AS b WHERE b.item.id = ?1 AND b.start <= CURRENT_TIMESTAMP")
    List<Booking> findAllCurrentByItemId(long itemId);
}
