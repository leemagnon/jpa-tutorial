package com.example.jpatutorial.service;

import com.example.jpatutorial.entity.Article;
import com.example.jpatutorial.entity.Content;
import com.example.jpatutorial.entity.Video;
import com.example.jpatutorial.repository.ArticleRepository;
import com.example.jpatutorial.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // Integrated content methods
    public Page<Content> getAllContents(int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortOrder : sort) {
            String[] parts = sortOrder.split(",");
            orders.add(new Sort.Order(
                    parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    parts[0]));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // Combine and convert to Page
        List<Content> contents = Stream.concat(
                articleRepository.findAll().stream(),
                videoRepository.findAll().stream()).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), contents.size());

        return new org.springframework.data.domain.PageImpl<>(
                contents.subList(start, end),
                pageable,
                contents.size());
    }

    public Content getContentById(Long id) {
        // Try to find as Article first
        return articleRepository.findById(id)
                .map(article -> (Content) article)
                .orElseGet(() -> videoRepository.findById(id)
                        .map(video -> (Content) video)
                        .orElseThrow(() -> new RuntimeException("Content not found with id: " + id)));
    }

    public Page<Content> getContentsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Content> contents = Stream.concat(
                articleRepository.findByAuthorId(userId).stream(),
                videoRepository.findByAuthorId(userId).stream()).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), contents.size());

        return new org.springframework.data.domain.PageImpl<>(
                contents.subList(start, end),
                pageable,
                contents.size());
    }

    public Page<Content> searchContents(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Content> contents = Stream.concat(
                articleRepository.findByTitleContainingIgnoreCase(keyword).stream(),
                videoRepository.findByTitleContainingIgnoreCase(keyword).stream()).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), contents.size());

        return new org.springframework.data.domain.PageImpl<>(
                contents.subList(start, end),
                pageable,
                contents.size());
    }
}