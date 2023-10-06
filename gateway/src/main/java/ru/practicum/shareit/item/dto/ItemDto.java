package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validators.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 512)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;
}
