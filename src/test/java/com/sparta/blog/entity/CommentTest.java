package com.sparta.blog.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    @DisplayName("Comment 생성 테스트")
    void testCreateCommentEntity() {
        // Given
        String content = "댓글";
        Board board = new Board();
        User user = new User();

        // When
        Comment comment = new Comment(content, board, user);

        // Then
        assertEquals(content, comment.getContent());
        assertEquals(board, comment.getBoard());
        assertEquals(user, comment.getUser());
    }

    @Test
    @DisplayName("Comment 수정 테스트")
    void testUpdateCommentContent() {
        // Given
        String content = "댓글";
        String updatedContent = "댓글(수정)";
        Comment comment = new Comment(content, null, null);

        // When
        comment.updateContent(updatedContent);

        // Then
        assertEquals(updatedContent, comment.getContent());
    }
}
