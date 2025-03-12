package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {
} 