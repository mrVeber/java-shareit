package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class CommentDtoRequest {
    private long id;

    @NotBlank
    @Size(max = 500)
    private String text;
}
