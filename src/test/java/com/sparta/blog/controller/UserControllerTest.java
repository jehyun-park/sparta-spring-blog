package com.sparta.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blog.dto.UserRequestDto;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    UserService userService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new UserController(userService,jwtUtil))
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("회원가입")
    void test1() throws Exception {
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
}
