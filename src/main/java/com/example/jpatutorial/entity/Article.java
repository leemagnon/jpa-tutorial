package com.example.jpatutorial.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "articles")
public class Article extends Content {
    private String body;
    private String category;

    // getters and setters
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
} 