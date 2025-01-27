package com.sparta.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blog.dto.UserRequestDto;
import com.sparta.blog.dto.UserUpdateRequestDto;
import com.sparta.blog.entity.User;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.mvc.MockSpringSecurityFilter;
import com.sparta.blog.repository.UserRepository;
import com.sparta.blog.security.UserDetailsImpl;
import com.sparta.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    private MockMvc mvc;

    private Principal mockPrincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new UserController(userService, jwtUtil))
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup() {
        // Mock 테스트 유져 생성
        String username = "user";
        String password = "password123";
        String email = "test@naver.com";
        String info = "info1";
        User testUser = new User(username, password, email, info);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }


    @Test
    @DisplayName("회원가입")
    void testSignup() throws Exception {
        //given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("testuser@sparta.com");
        userRequestDto.setPassword("password123");
        userRequestDto.setUsername("testuser1");
        userRequestDto.setInfo("info1");

        //when-then
        mvc.perform(post("/api/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("사용자 info 업데이트")
    void testUpdateUserInfo() throws Exception {
        // Given
        mockUserSetup();

        Long userId = 1L;
        String token = "test_token";
        String updatedInfo = "info 업뎃";

        // When
        when(jwtUtil.getEmailFromToken(token)).thenReturn("test@naver.com");

        // Then
        mvc.perform(put("/api/users/{id}/info", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"info\": \"" + updatedInfo + "\", \"email\": \"test@example.com\", \"password\": \"password123\", \"username\": \"testuser\"}")) // 업데이트할 정보 설정
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("사용자 비밀번호 업데이트")
    void testUpdateUserPassword() throws Exception {
        // Given
        mockUserSetup();

        Long userId = 1L;
        String token = "test_token";
        String oldPassword = "password123";
        String newPassword = "newPassword123";

        UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
        userUpdateRequestDto.setOldPassword(oldPassword);
        userUpdateRequestDto.setNewPassword(newPassword);
        userUpdateRequestDto.setNewPasswordCheck(newPassword);

        // When
        when(jwtUtil.getEmailFromToken(token)).thenReturn("test@naver.com");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Then
        mvc.perform(put("/api/users/{id}/password", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequestDto)))
                .andExpect(status().isOk());
    }
}