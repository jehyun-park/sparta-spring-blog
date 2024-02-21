package com.sparta.blog.service;

import com.sparta.blog.dto.*;
import com.sparta.blog.entity.User;
import com.sparta.blog.exception.PasswordNotMatchedException;
import com.sparta.blog.exception.UserNotMatchedException;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HttpServletRequest request;

    public void signUp(UserRequestDto userRequestDto){
        String email = userRequestDto.getEmail();
        String username = userRequestDto.getUsername();
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        String info = userRequestDto.getInfo();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateKeyException("이미 존재하는 이메일입니다.");
        }

        User user = new User(email, password, username, info);
        userRepository.save(user);
    }

    public void signIn(UserLoginRequestDto userLoginRequestDto) {
        String email = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("등록된 유저가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchedException();
        }

    }

    //프로필 조회
    @Transactional(readOnly = true)
    public UserResponseDto getUserList(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("회원 정보가 존재하지 않습니다.")
        );
        return new UserResponseDto(user);
    }

    // info 수정
    @Transactional
    public UserResponseDto updateUserInfo(String token, Long id, UserRequestDto userRequestDto) {
        User user = getUserByToken(token);
        if (!user.getId().equals(id)) {
            throw new UserNotMatchedException();
        }
        user.setInfo(userRequestDto.getInfo());
        userRepository.save(user);
        return new UserResponseDto(user);
    }

    //비밀번호 변경
    @Transactional
    public UserResponseDto updateUserPassword(String token, Long id, UserUpdateRequestDto userUpdateRequestDto) {
        User user = getUserByToken(token);
        if (!user.getId().equals(id)) {
            throw new UserNotMatchedException();
        }
        if (userUpdateRequestDto.getOldPassword().equals(userUpdateRequestDto.getNewPassword())) {
            throw new IllegalArgumentException("새로운 비밀번호는 현재 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userUpdateRequestDto.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (!userUpdateRequestDto.getNewPassword().equals(userUpdateRequestDto.getNewPasswordCheck())) {
            throw new BadCredentialsException("새 비밀번호와 확인이 일치하지 않습니다.");
        }
        String newPassword = passwordEncoder.encode(userUpdateRequestDto.getNewPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        return new UserResponseDto(user);
    }


    private User getUserByToken(String token) {
        String email = jwtUtil.getEmailFromToken(token);

        return userRepository.findByEmail(email).orElseThrow(
                ()-> new NoSuchElementException("사용자를 찾을 수 없습니다.")
        );
    }
}
