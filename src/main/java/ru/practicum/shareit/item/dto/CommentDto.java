package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

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
    @Size(max = 512, groups = {Create.class, Update.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
