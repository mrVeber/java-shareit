package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    @NotBlank(groups = {Create.class})
    private final String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private final String email;
    private long id;
}
