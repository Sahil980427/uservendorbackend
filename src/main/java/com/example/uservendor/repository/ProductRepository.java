package com.example.uservendor.repository;
import com.example.uservendor.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // This custom query lets us instantly grab all products for a specific vendor's dashboard
    List<Product> findByVendorProfileId(Long vendorProfileId);
}