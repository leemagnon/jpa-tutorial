package com.example.jpatutorial.controller;

import com.example.jpatutorial.entity.Article;
import com.example.jpatutorial.entity.Video;
import com.example.jpatutorial.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ContentController {

    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    // Article endpoints
    @PostMapping("/articles")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(contentService.createArticle(article));
    }

    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(contentService.getAllArticles());
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getArticleById(id));
    }

    @GetMapping("/articles/category/{category}")
    public ResponseEntity<List<Article>> getArticlesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(contentService.getArticlesByCategory(category));
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return ResponseEntity.ok(contentService.updateArticle(id, article));
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        contentService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    // Video endpoints
    @PostMapping("/videos")
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        return ResponseEntity.ok(contentService.createVideo(video));
    }

    @GetMapping("/videos")
    public ResponseEntity<List<Video>> getAllVideos() {
        return ResponseEntity.ok(contentService.getAllVideos());
    }

    @GetMapping("/videos/{id}")
    public ResponseEntity<Video> getVideo(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getVideoById(id));
    }

    @GetMapping("/videos/short/{maxDuration}")
    public ResponseEntity<List<Video>> getShortVideos(@PathVariable Integer maxDuration) {
        return ResponseEntity.ok(contentService.getShortVideos(maxDuration));
    }

    @PutMapping("/videos/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        return ResponseEntity.ok(contentService.updateVideo(id, video));
    }

    @DeleteMapping("/videos/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        contentService.deleteVideo(id);
        return ResponseEntity.ok().build();
    }

    // Integrated content endpoints
    @GetMapping("/contents")
    public ResponseEntity<?> getAllContents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        return ResponseEntity.ok(contentService.getAllContents(page, size, sort));
    }

    @GetMapping("/contents/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContentById(id));
    }

    @GetMapping("/users/{userId}/contents")
    public ResponseEntity<?> getUserContents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(contentService.getContentsByUserId(userId, page, size));
    }

    @GetMapping("/contents/search")
    public ResponseEntity<?> searchContents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(contentService.searchContents(keyword, page, size));
    }
}