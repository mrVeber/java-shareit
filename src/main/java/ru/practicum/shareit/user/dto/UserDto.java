package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validators.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDto {
    private long id;
    @NotBlank(groups = Create.class, message = "Received user with empty name")
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;
    @Email(groups = {Create.class, Update.class}, message = "Incorrect email address")
    @NotEmpty(groups = Create.class, message = "Email not specified")
    @EqualsAndHashCode.Include
    @Size(max = 512, groups = {Create.class, Update.class})
    private String email;
}
