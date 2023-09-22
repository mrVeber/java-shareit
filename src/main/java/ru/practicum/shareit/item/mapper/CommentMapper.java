package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDtoInput;
import ru.practicum.shareit.item.dto.CommentDtoOutput;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


@UtilityClass
public class CommentMapper {
    public Comment toComment(CommentDtoInput commentDto, User author, Item item) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                author,
                item,
                commentDto.getCreated());
    }

    public CommentDtoInput toCommentDtoInput(Comment comment) {
        return new CommentDtoInput(comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public CommentDtoOutput toCommentDtoOutput(Comment comment) {
        return new CommentDtoOutput(comment.getId(),
                comment.getText(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}