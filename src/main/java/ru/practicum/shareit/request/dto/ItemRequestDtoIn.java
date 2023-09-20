package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoIn {
    @NotBlank(groups = {Create.class, Update.class})
    @Size(max = 1000, groups = {Create.class, Update.class})
    private String description;
}
