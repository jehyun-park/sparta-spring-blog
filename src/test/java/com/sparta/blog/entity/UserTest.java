package com.sparta.blog.entity;

import com.sparta.blog.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {

    @Test
    @DisplayName("User 엔티티 생성 테스트")
    void testCreateUserEntity() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";
        String info = "user info";

        // when
        User user = User.builder()
                .email(email)
                .password(password)
                .username(username)
                .info(info)
                .build();

        // Then
        assertNotNull(user);
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(username, user.getUsername());
        assertEquals(info, user.getInfo());
    }
}
