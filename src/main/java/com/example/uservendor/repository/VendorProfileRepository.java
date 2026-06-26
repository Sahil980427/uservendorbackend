package com.example.uservendor.repository;
import com.example.uservendor.entity.VendorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {
    // Add this to VendorProfileRepository.java
    java.util.Optional<com.example.uservendor.entity.VendorProfile> findByUserId(Long userId);
}