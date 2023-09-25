package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemId);

    List<Comment> findAllByItemIdIn(List<Long> itemIdList, Sort sort);

}