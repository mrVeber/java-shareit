package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoFullResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@Rollback
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemIntegrationTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;

    private final long ownerId = 1L;
    private final String ownerName = "ownerUser";
    private final String ownerMail = "owner@mail.com";
    private final UserDtoRequest requestOwnerDto = UserDtoRequest.builder().name(ownerName).email(ownerMail).build();

    private final long itemId = 1L;
    private final String itemName = "itemTest";
    private final String itemDescription = "itemTestTestTest";
    private final ItemDtoRequest requestItemDto = ItemDtoRequest.builder().name(itemName).description(itemDescription)
            .available(true).requestId(0).build();

    private final String newItemName = "newItemTest";
    private final String newItemDescription = "newItemTestTestTest";
    private final ItemDtoRequest newDataItemDto = ItemDtoRequest.builder().name(newItemName)
            .description(newItemDescription).available(true).requestId(0).build();

    @Test
    void testCreateUser() {
        userService.create(requestOwnerDto);
        itemService.create(requestItemDto, ownerId);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", requestItemDto.getName()).getSingleResult();

        assertThat(item.getId(), equalTo(itemId));
        assertThat(item.getOwner().getId(), equalTo(ownerId));
        assertThat(item.getName(), equalTo(requestItemDto.getName()));
        assertThat(item.getDescription(), equalTo(requestItemDto.getDescription()));
        assertThat(item.isAvailable(), equalTo(requestItemDto.getAvailable()));
    }

    @Test
    void testGetItemById() {
        userService.create(requestOwnerDto);
        itemService.create(requestItemDto, ownerId);
        ItemDtoFullResponse responseItemDto = itemService.getById(itemId, ownerId);

        assertThat(responseItemDto.getId(), equalTo(itemId));
        assertThat(responseItemDto.getName(), equalTo(requestItemDto.getName()));
        assertThat(responseItemDto.getDescription(), equalTo(requestItemDto.getDescription()));
        assertThat(responseItemDto.getAvailable(), equalTo(requestItemDto.getAvailable()));
    }

    @Test
    void testGetItemsOneOwner() {
        userService.create(requestOwnerDto);
        itemService.create(requestItemDto, ownerId);
        List<ItemDtoFullResponse> responseItemDtoList = itemService.getItemsOneOwner(ownerId, 0,10);
        ItemDtoFullResponse responseItemDto = responseItemDtoList.get(0);

        assertThat(responseItemDtoList.size(),  equalTo(1));
        assertThat(responseItemDto.getName(), equalTo(requestItemDto.getName()));
        assertThat(responseItemDto.getDescription(), equalTo(requestItemDto.getDescription()));
        assertThat(responseItemDto.getAvailable(), equalTo(requestItemDto.getAvailable()));
    }

    @Test
    void testUpdateItem() {
        userService.create(requestOwnerDto);
        itemService.create(requestItemDto, ownerId);

        List<ItemDtoFullResponse> responseItemDtoList = itemService.getItemsOneOwner(ownerId, 0,10);
        assertThat(responseItemDtoList.size(),  equalTo(1));
        assertThat(responseItemDtoList.get(0).getName(), equalTo(requestItemDto.getName()));

        itemService.update(itemId, newDataItemDto, ownerId);
        responseItemDtoList = itemService.getItemsOneOwner(ownerId, 0,10);
        assertThat(responseItemDtoList.size(),  equalTo(1));
        assertThat(responseItemDtoList.get(0).getName(), equalTo(newDataItemDto.getName()));
    }

    @Test
    void testDeleteItem() {
        userService.create(requestOwnerDto);
        itemService.create(requestItemDto, ownerId);

        List<ItemDtoFullResponse> responseItemDtoList = itemService.getItemsOneOwner(ownerId, 0,10);
        assertThat(responseItemDtoList.size(),  equalTo(1));
        assertThat(responseItemDtoList.get(0).getName(), equalTo(requestItemDto.getName()));

        itemService.delete(itemId, ownerId);
        responseItemDtoList = itemService.getItemsOneOwner(ownerId, 0,10);
        assertThat(responseItemDtoList.size(),  equalTo(0));
    }
}
