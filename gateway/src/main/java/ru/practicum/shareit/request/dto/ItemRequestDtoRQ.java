package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.validators.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ItemRequestDtoRQ {
    private Long id;
    private Long requesterId;
    @NotBlank(groups = Create.class)
    @Size(groups = Create.class, max = 256)
    private String description;
    private LocalDateTime created;
}
