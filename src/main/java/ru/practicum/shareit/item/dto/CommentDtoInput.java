//package ru.practicum.shareit.item.dto;
//
//import lombok.*;
//import ru.practicum.shareit.validators.Create;
//import ru.practicum.shareit.validators.Update;
//
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.time.LocalDateTime;
//
//@Setter
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//public class CommentDtoInput {
//
//    private Long id;
//
//    @NotBlank(groups = Create.class)
//    @Size (max = 4000, groups = {Create.class, Update.class})
//    private String text;
//
//    private Long authorId;
//
//    private String authorName;
//
//    private LocalDateTime created;
//}
