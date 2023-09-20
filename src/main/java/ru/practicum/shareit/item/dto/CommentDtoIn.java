package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDtoIn {
    @Size(max = 1000, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String text;
}