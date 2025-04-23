package com.example.jpatutorial.dto;

import com.example.jpatutorial.entity.UserProfile;

public class UserProfileDTO {
    private Long id;
    private String bio;
    private String profileImage;

    public UserProfileDTO() {
    }

    public UserProfileDTO(UserProfile profile) {
        this.id = profile.getId();
        this.bio = profile.getBio();
        this.profileImage = profile.getProfileImage();
    }

    public Long getId() {
        return id;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileImage() {
        return profileImage;
    }
}