package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    private long id;
    @NotBlank(groups = Create.class)
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;
    @NotBlank(groups = Create.class)
    @Size(max = 1000, groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long requestId;

}