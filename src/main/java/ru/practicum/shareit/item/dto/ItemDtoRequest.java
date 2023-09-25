package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.*;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class ItemDtoRequest {
    @NotBlank(groups = Create.class)
    @Size(max = 50, groups = {Create.class, Update.class})
    private String name;

    @NotBlank(groups = Create.class)
    @Size(max = 500, groups = {Create.class, Update.class})
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;

    @PositiveOrZero
    private long requestId;
}
