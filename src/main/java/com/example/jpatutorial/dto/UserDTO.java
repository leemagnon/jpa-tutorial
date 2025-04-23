package com.example.jpatutorial.dto;

import com.example.jpatutorial.entity.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserDTO {
    private Long id;
    private String name;
    private List<PostDTO> posts;
    private UserProfileDTO profile;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();

        if (user.getPosts() != null) {
            this.posts = user.getPosts().stream()
                    .map(PostDTO::new)
                    .collect(Collectors.toList());
        }

        if (user.getProfile() != null) {
            this.profile = new UserProfileDTO(user.getProfile());
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public UserProfileDTO getProfile() {
        return profile;
    }
}