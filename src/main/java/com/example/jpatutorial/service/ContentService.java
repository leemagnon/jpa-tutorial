package com.example.jpatutorial.service;

import com.example.jpatutorial.entity.Article;
import com.example.jpatutorial.entity.Video;
import com.example.jpatutorial.repository.ArticleRepository;
import com.example.jpatutorial.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentService {
    
    private final ArticleRepository articleRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public ContentService(ArticleRepository articleRepository, VideoRepository videoRepository) {
        this.articleRepository = articleRepository;
        this.videoRepository = videoRepository;
    }

    // Article 관련 메서드
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public List<Article> getArticlesByCategory(String category) {
        return articleRepository.findByCategory(category);
    }

    public Article updateArticle(Long id, Article articleDetails) {
        Article article = getArticleById(id);
        article.setTitle(articleDetails.getTitle());
        article.setBody(articleDetails.getBody());
        article.setCategory(articleDetails.getCategory());
        return articleRepository.save(article);
    }

    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // Video 관련 메서드
    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getShortVideos(Integer maxDuration) {
        return videoRepository.findByDurationLessThan(maxDuration);
    }

    public Video updateVideo(Long id, Video videoDetails) {
        Video video = getVideoById(id);
        video.setTitle(videoDetails.getTitle());
        video.setUrl(videoDetails.getUrl());
        video.setDuration(videoDetails.getDuration());
        return videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }
} 