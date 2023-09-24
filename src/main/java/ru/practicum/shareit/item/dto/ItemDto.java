package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.validators.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = Create.class)
    @Size (max = 50)
    private String name;
    @NotBlank(groups = Create.class)
    @Size (max = 200)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;
}
