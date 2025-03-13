package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByCategory(String category);

    List<Article> findByAuthorId(Long authorId);

    List<Article> findByTitleContainingIgnoreCase(String keyword);
}