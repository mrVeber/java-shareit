package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequester(Long userId);


    @Query(nativeQuery = true,
            value = "select * from REQUESTS where REQUESTS.requester_id !=?1",
            countQuery = "select count(*) from REQUESTS where REQUESTS.requester_id !=?1")
    Page<ItemRequest> findAllWithoutUserId(Long userId, Pageable pageable);

    Page<ItemRequest> findRequestsByRequesterNotOrderByCreatedDesc(Long requester, Pageable pageable);


    Optional<ItemRequest> findRequestById(long requestId);
}
