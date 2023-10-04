//package ru.practicum.shareit.resource;
//
//import ru.practicum.shareit.item.model.Item;
//
//public class ItemData {
//
//    public static ItemDto getItemDto() {
//        return ItemDto.builder()
//                .id(1L)
//                .name("name")
//                .description("description")
//                .available(true)
//                .requestId(null)
//                .build();
//    }
//
//    public static Item getItem() {
//        return Item.builder()
//                .id(1L)
//                .itemRequest(null)
//                .owner(null)
//                .name("name")
//                .description("description")
//                .available(true)
//                .build();
//    }
//
//    public static ItemDto getItemDtoForUpdate() {
//        return ItemDto.builder()
//                .id(1L)
//                .name("updated")
//                .description("updated description")
//                .available(true)
//                .requestId(null)
//                .build();
//    }
//}