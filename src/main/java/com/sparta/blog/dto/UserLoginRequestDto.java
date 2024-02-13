package com.sparta.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class UserLoginRequestDto {
    @NotBlank(message = "이메일은 필수로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수로 입력해야 합니다.")
    private String password;
}
