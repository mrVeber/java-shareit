//package ru.practicum.shareit.resource;
//
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.dto.UserDto;
//
//public class UserData {
//    public static User getUser() {
//        return User.builder()
//                .id(1L)
//                .name("test_nazar")
//                .email("test_nazar@mail.ru")
//                .build();
//    }
//
//    public static UserDto getUserDto() {
//        return UserDto.builder()
//                .id(1L)
//                .name("test_nazar")
//                .email("test_nazar@mail.ru")
//                .build();
//    }
//
//    public static User getUpdateUser() {
//        return User.builder()
//                .id(1L)
//                .name("test_updated")
//                .email("updated@mail.ru")
//                .build();
//    }
//
//    public static UserDto getUpdateDtoUser() {
//        return UserDto.builder()
//                .id(1L)
//                .name("test_updated")
//                .email("updated@mail.ru")
//                .build();
//    }
//}