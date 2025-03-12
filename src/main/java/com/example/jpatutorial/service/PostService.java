package com.example.jpatutorial.service;

import com.example.jpatutorial.entity.Post;
import com.example.jpatutorial.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Create
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    // Read
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    // Update
    public Post updatePost(Long id, Post postDetails) {
        Post post = getPostById(id);
        post.setContent(postDetails.getContent());
        post.setUser(postDetails.getUser());
        return postRepository.save(post);
    }

    // Delete
    public void deletePost(Long id) {
        Post post = getPostById(id);
        postRepository.delete(post);
    }
} 