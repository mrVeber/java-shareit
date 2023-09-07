package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String email;
}
