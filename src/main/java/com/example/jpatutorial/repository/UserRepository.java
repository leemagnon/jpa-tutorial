package com.example.jpatutorial.repository;

import com.example.jpatutorial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
