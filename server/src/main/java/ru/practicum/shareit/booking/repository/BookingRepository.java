package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where ?2 between booking.start " +
            "and booking.end " +
            "and booking.booker.id = ?1 ")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime now, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.booker.id = ?1 ")
    List<Booking> findByBookerPast(long userId, LocalDateTime end, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking "
            + "where booking.start > ?2 "
            + "and booking.booker.id = ?1 ")
    List<Booking> findByBookerFuture(long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.booker.id = ?1 ")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status, Sort sort, PageRequest pageRequest);

    List<Booking> findByItemOwnerId(long ownerId, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where ?2 between booking.start " +
            "and booking.end " +
            "and booking.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerCurrent(long userId, LocalDateTime now, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where booking.end < ?2 " +
            "and booking.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerPast(long userId, LocalDateTime end, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where booking.start > ?2 " +
            "and booking.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerFuture(long userId, LocalDateTime start, Sort sort, PageRequest pageRequest);

    @Query("select booking from Booking booking " +
            "where booking.status = ?2 " +
            "and booking.item.owner.id = ?1 ")
    List<Booking> findByItemOwnerAndStatus(long userId, BookingStatus status, Sort sort, PageRequest pageRequest);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end, Sort sort);

    @Query("select distinct booking " +
            "from Booking booking " +
            "where booking.start <= :now " +
            "and booking.item.id in :ids " +
            "and booking.status = 'APPROVED' " +
            "and booking.item.owner.id = :userId ")
    List<Booking> findBookingsLast(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId,
                                   Sort sort);

    @Query("select distinct booking " +
            "from Booking booking " +
            "where booking.start > :now " +
            "and booking.status = 'APPROVED' " +
            "and booking.item.id in :ids " +
            "and booking.item.owner.id = :userId ")
    List<Booking> findBookingsNext(@Param("ids") List<Long> ids,
                                   @Param("now") LocalDateTime now,
                                   @Param("userId") long userId,
                                   Sort sort);

    @Query("select distinct booking " +
            "from Booking booking " +
            "where booking.status = 'APPROVED' " +
            "and booking.item.id in :ids ")
    List<Booking> findApprovedForItems(@Param("ids") List<Item> items,
                                       Sort sort);
}