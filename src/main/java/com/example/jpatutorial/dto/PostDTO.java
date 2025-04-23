package com.example.jpatutorial.dto;

import com.example.jpatutorial.entity.Post;

public class PostDTO {
    private Long id;
    private String content;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}