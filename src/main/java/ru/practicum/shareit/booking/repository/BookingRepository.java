package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Modifying
    @Query("UPDATE Booking i SET i.status = :status WHERE i.id = :bookingId")
    void updateStatus(@Param("status") BookingStatus status, @Param("bookingId") Long bookingId);

    List<Booking> findAllByBookerId(long bookerId);

    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime end);

    List<Booking> findAllByItemIdAndStatus(long itemId, BookingStatus status);

    Optional<Booking> findFirstByItemIdAndBookerIdAndStatusAndEndBefore(long itemId, long bookerId,
                                                                        BookingStatus status, LocalDateTime end);

    @Query (value = "SELECT i FROM Booking i" +
            "WHERE i.item_id  = :idItem " +
            "AND i.start_date < now() " +
            "ORDER BY i.end_date DESC " +
            "LIMIT 1", nativeQuery = true)
    Booking getLastBooking(@Param("idItem") Long idItem);

    @Query (value = "SELECT i FROM Booking i " +
            "WHERE i.item_id  = :idItem " +
            "AND i.start_date > :time " +
            "ORDER BY i.start_date ASC " +
            "LIMIT 1", nativeQuery = true)
    Booking getNextBooking(@Param("idItem") Long idItem,
                           @Param("time") LocalDateTime time);

    @Query(value = "SELECT i FROM Booking i " +
            "WHERE i.booker.id = :bookerId " +
            "AND i.start < :dateTime " +
            "AND i.end > :dateTime " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(@Param("bookerId")long bookerId,
                                                             @Param("dateTime")LocalDateTime dateTime);

    @Query(value = "SELECT i FROM Booking i " +
            "join fetch i.item AS a " +
            "join fetch a.owner AS o " +
            " WHERE o.id = :ownerId " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId);

    @Query(value = "SELECT i " +
            "FROM Booking i " +
            "join fetch i.item AS a join fetch a.owner AS o " +
            " WHERE o.id = :ownerId " +
            "AND i.status = :status " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    @Query(value = "SELECT i FROM Booking i " +
            "join fetch i.item AS a " +
            "join fetch a.owner AS o " +
            "WHERE o.id = :ownerId " +
            "AND i.start > :dateTime  " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByOwnerIdAndStartAfter(@Param("ownerId") Long ownerId,
                                                @Param("dateTime") LocalDateTime dateTime);

    @Query(value = "SELECT i FROM Booking i " +
            "join fetch i.item AS a " +
            "join fetch a.owner AS o " +
            " WHERE o.id = :ownerId " +
            "AND i.end < :dateTime  " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByOwnerIdAndEndBefore(@Param("ownerId") Long ownerId,
                                               @Param("dateTime") LocalDateTime dateTime);

    @Query(value = "SELECT i FROM Booking i " +
            "join fetch i.item AS a " +
            "join fetch a.owner AS o " +
            " WHERE o.id = :ownerId " +
            "AND i.start < :dateTime " +
            "AND i.end > :dateTime " +
            "ORDER BY i.start DESC")
    List<Booking> findAllByOwnerIdAndStartBeforeAndEndAfter(@Param("ownerId") Long ownerId,
                                                            @Param("dateTime") LocalDateTime dateTime);
}
