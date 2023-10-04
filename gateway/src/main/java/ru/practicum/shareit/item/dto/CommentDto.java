package ru.practicum.shareit.item.dto;

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
public class CommentDto {
    private Long id;
    @NotBlank(groups = Create.class)
    @Size(min = 5, max = 100)
    private String text;
    private String authorName;
    private LocalDateTime created;
}
