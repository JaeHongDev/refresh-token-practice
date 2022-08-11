package com.artisan.example.springboot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);
}
