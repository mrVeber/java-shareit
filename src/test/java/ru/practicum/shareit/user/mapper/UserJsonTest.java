package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.dto.UserDtoResponse;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserJsonTest {
    @Autowired
    private JacksonTester<UserDtoRequest> jsonRequest;
    @Autowired
    private JacksonTester<UserDtoResponse> jsonResponse;

    private final long userId = 10L;
    private final String userName = "userUser";
    private final String userMail = "user@mail.com";
    private final UserDtoRequest requestUserDto = UserDtoRequest.builder().name(userName).email(userMail).build();
    private final UserDtoResponse responseUserDto = UserDtoResponse.builder().id(userId).name(userName)
            .email(userMail).build();

    @Test
    void testRequestUserDto() throws Exception {
        String jsonString = "{\"name\": \"userUser\", " +
                "\"email\": \"user@mail.com\"}";

        UserDtoRequest result = jsonRequest.parseObject(jsonString);
        assertThat(result).isEqualTo(requestUserDto);
    }

    @Test
    void testResponseUserDto() throws Exception {
        JsonContent<UserDtoResponse> result = jsonResponse.write(responseUserDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(responseUserDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(responseUserDto.getEmail());
    }
}
