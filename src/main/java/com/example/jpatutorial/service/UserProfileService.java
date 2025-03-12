package com.example.jpatutorial.service;

import com.example.jpatutorial.entity.UserProfile;
import com.example.jpatutorial.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    
    private final UserProfileRepository profileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public UserProfile createProfile(UserProfile profile) {
        return profileRepository.save(profile);
    }

    public UserProfile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
    }

    public UserProfile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    public UserProfile updateProfile(Long id, UserProfile profileDetails) {
        UserProfile profile = getProfileById(id);
        profile.setBio(profileDetails.getBio());
        profile.setProfileImage(profileDetails.getProfileImage());
        return profileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }
} 