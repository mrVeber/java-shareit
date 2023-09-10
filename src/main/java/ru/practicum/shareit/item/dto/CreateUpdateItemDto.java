package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

@Data
public class CreateUpdateItemDto {
    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @NotBlank(groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = {Create.class})
    private Boolean available;
}
