package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);
} 