package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validators.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @NotEmpty(groups = Create.class)
    @Size (max = 255, groups = {Create.class, Update.class})
    private String name;
    @NotEmpty(groups = Create.class)
    @Email(groups = {Create.class, Update.class})
    @Size (max = 512, groups = {Create.class, Update.class})
    private String email;
}
