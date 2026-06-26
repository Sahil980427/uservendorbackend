package com.example.uservendor.repository;
import com.example.uservendor.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring translates this into: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}