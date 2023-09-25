package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(Item item, CommentDtoRequest commentDto, User user, LocalDateTime currentMoment) {
        return Comment.builder()
                .text(commentDto.getText())
                .created(currentMoment)
                .item(item)
                .author(user)
                .build();
    }

    public CommentDtoResponse toResponseCommentDto(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .build();
    }
}