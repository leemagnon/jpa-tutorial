package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByDurationLessThan(Integer maxDuration);

    List<Video> findByAuthorId(Long authorId);

    List<Video> findByTitleContainingIgnoreCase(String keyword);
}