package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    List<Comment> findByParentCommentId(Long parentId);
    List<Comment> findByParentCommentIsNull();
} 