package com.example.jpatutorial.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "videos")
public class Video extends Content {
    private String url;
    private Integer duration;

    // getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
} 