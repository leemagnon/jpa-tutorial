package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // JpaRepository에서 기본적인 CRUD 메서드를 제공합니다
} 