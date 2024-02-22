package com.sparta.blog.entity;

import com.sparta.blog.dto.BoardRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoardTest {

    @Test
    @DisplayName("Board 생성 테스트")
    void testCreateBoardEntity() {
        // Given
        String title = "제목";
        String content = "내용";
        BoardRequestDto requestDto = new BoardRequestDto(title, content);
        User user = new User("test@example.com", "password123", "testuser", "user info");

        // When
        Board board = new Board(requestDto, user);

        // Then
        assertEquals(title, board.getTitle());
        assertEquals(content, board.getContent());
        assertEquals(user, board.getUser());
    }

    @Test
    @DisplayName("Board 수정 테스트")
    void testUpdateBoardEntity() {
        // Given
        String title = "제목";
        String content = "내용";
        String updatedTitle = "제목(수정)";
        String updatedContent = "내용(수정)";
        BoardRequestDto requestDto = new BoardRequestDto(title, content);
        Board board = new Board(requestDto, null);

        // When
        BoardRequestDto updatedRequestDto = new BoardRequestDto(updatedTitle, updatedContent);
        board.update(updatedRequestDto);

        // Then
        assertEquals(updatedTitle, board.getTitle());
        assertEquals(updatedContent, board.getContent());
    }
}
