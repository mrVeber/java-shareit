package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.model.Comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface CommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto fromCommentToDto(Comment comment);

    Comment fromCreateDtoToComment(CreateCommentDto dto);
}