package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.validators.Create;
import ru.practicum.shareit.validators.Update;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@Setter
@ToString
public class UserDto {
    private long id;
    @NotBlank(groups = Create.class)

    private String name;

    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    @Size(max = 512)
    private String email;
}
