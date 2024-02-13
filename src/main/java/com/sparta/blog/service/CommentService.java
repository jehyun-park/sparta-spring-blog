package com.sparta.blog.service;

import com.sparta.blog.dto.CommentRequestDto;
import com.sparta.blog.dto.CommentResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.entity.Comment;
import com.sparta.blog.entity.User;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.repository.BoardRepository;
import com.sparta.blog.repository.CommentRepository;
import com.sparta.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final JwtUtil jwtUtil;



    public CommentResponseDto review(CommentRequestDto commentRequestDto,Long boardId,String token){
        User user = getUserByToken(token);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("없는 게시물 입니다."));

        Comment comment = new Comment(commentRequestDto.getContent(), board, user);
        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }


    public CommentResponseDto updateComment(CommentRequestDto commentRequestDto,Long commentId, String token){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("등록된 댓글이 아닙니다."));
        User user = getUserByToken(token);

        //자신이 작성한 댓글인지 아닌지 확인
        isCommentMyselfValidate(user, comment);

        comment.updateContent(commentRequestDto.getContent());
        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long commentId,String token){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("등록된 댓글이 아닙니다."));

        User user = getUserByToken(token);

        isCommentMyselfValidate(user, comment);

        commentRepository.delete(comment);
    }

    private void isCommentMyselfValidate(User user, Comment comment) {
        if(!(user.equals(comment.getUser()))){
            throw new IllegalArgumentException("자신이 작성한 댓글이 아닙니다.");
        }
    }

    private User getUserByToken(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        return user;
    }



}